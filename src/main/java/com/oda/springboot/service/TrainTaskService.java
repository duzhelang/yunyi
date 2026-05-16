package com.oda.springboot.service;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oda.springboot.entity.Files;
import com.oda.springboot.entity.ModelVersion;
import com.oda.springboot.entity.TrainTask;
import com.oda.springboot.mapper.FileMapper;
import com.oda.springboot.mapper.ModelVersionMapper;
import com.oda.springboot.mapper.TrainTaskMapper;
import com.oda.springboot.utils.UsePythonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TrainTaskService extends ServiceImpl<TrainTaskMapper, TrainTask> {

    private static final Log log = Log.get();

    @Autowired
    private TrainTaskMapper trainTaskMapper;

    @Autowired
    private ModelVersionMapper modelVersionMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UsePythonUtils usePythonUtils;

    /**
     * 默认数据集映射（前端传入 default_ 开头的 ID 时使用）
     */
    private static final java.util.Map<String, String> DEFAULT_DATASET_MAP = new java.util.HashMap<>();
    static {
        DEFAULT_DATASET_MAP.put("default_v1", "diabetes_train_dataset.csv");
        DEFAULT_DATASET_MAP.put("default_extended", "diabetes_features_extended.csv");
        DEFAULT_DATASET_MAP.put("default_history", "patient_history_data.csv");
    }

    /**
     * 创建并启动训练任务
     */
    public TrainTask createAndStartTask(Map<String, Object> params) {
        Object trainFileIdObj = params.get("trainFileId");
        String modelName = (String) params.get("modelName");
        Map<String, Object> hyperParams = (Map<String, Object>) params.get("hyperParams");
        String pythonScript = (String) params.get("pythonScript");
        Map<String, Object> incrementalParams = (Map<String, Object>) params.get("incrementalParams");

        Integer trainFileId = null;
        String trainFileName = null;

        // 处理 trainFileId：支持 Integer（数据库ID）和 String（默认数据集ID）
        if (trainFileIdObj instanceof String) {
            String trainFileIdStr = (String) trainFileIdObj;
            if (DEFAULT_DATASET_MAP.containsKey(trainFileIdStr)) {
                // 使用默认数据集
                trainFileName = DEFAULT_DATASET_MAP.get(trainFileIdStr);
                log.info("使用默认数据集: {}", trainFileName);
            } else {
                // 尝试解析为数据库ID
                try {
                    trainFileId = Integer.parseInt(trainFileIdStr);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("无效的训练文件ID: " + trainFileIdStr);
                }
            }
        } else if (trainFileIdObj instanceof Integer) {
            trainFileId = (Integer) trainFileIdObj;
        }

        // 如果是数据库ID，从数据库获取文件信息
        if (trainFileId != null) {
            Files trainFile = fileMapper.selectById(trainFileId);
            if (trainFile == null) {
                throw new RuntimeException("训练文件不存在");
            }
            trainFileName = trainFile.getName();
        }

        if (trainFileName == null || trainFileName.isEmpty()) {
            throw new RuntimeException("请选择训练数据集");
        }

        // 创建训练任务记录
        // 将增量训练参数合并到 hyperParams 中保存
        String hyperParamsJson = JSONUtil.toJsonStr(hyperParams);
        if (incrementalParams != null && pythonScript != null && pythonScript.toLowerCase().contains("incremental")) {
            com.alibaba.fastjson.JSONObject hyperObj = com.alibaba.fastjson.JSON.parseObject(hyperParamsJson);
            hyperObj.put("incrementalParams", incrementalParams);
            hyperParamsJson = hyperObj.toJSONString();
        }

        TrainTask task = new TrainTask();
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        task.setTaskName(modelName + "_" + timestamp);
        task.setTrainFileId(trainFileId);
        task.setTrainFileName(trainFileName);
        task.setModelName(modelName);
        task.setHyperParams(hyperParamsJson);
        task.setPythonScript(pythonScript != null ? pythonScript : "train.py");
        task.setStatus("pending");
        task.setCreateTime(new Date());

        int result = trainTaskMapper.insert(task);
        if (result > 0) {
            // 异步执行训练
            executeTrainingAsync(task);
            return task;
        } else {
            throw new RuntimeException("创建训练任务失败");
        }
    }

    /**
     * 异步执行训练任务
     */
    @Async
    public void executeTrainingAsync(TrainTask task) {
        try {
            // 更新任务状态为运行中
            task.setStatus("running");
            task.setStartTime(new Date());
            trainTaskMapper.updateById(task);
            log.info("训练任务开始执行: {}, 使用脚本: {}", task.getTaskName(), task.getPythonScript());

            // 执行训练脚本并捕获输出
            String processId = UUID.randomUUID().toString();
            String[] arguments = buildTrainingArguments(task);

            final StringBuilder outputCapture = new StringBuilder();
            usePythonUtils.callPythonWithCallback(processId, arguments, (line, isError) -> {
                outputCapture.append(line).append("\n");
                log.debug("训练日志: {}", line);
            });

            // 训练完成
            task.setStatus("completed");
            task.setEndTime(new Date());

            // 设置模型输出路径（使用 data/models/pth_models/ 目录）
            String projectRoot = System.getProperty("user.dir");
            String modelPath = projectRoot + File.separator + "data" + File.separator + "models" + File.separator + "pth_models" + File.separator + task.getModelName() + ".pth";
            task.setModelOutputPath(modelPath);
            
            // 解析 Python 输出中的真实训练指标
            boolean metricsParsed = false;
            String output = outputCapture.toString();
            for (String line : output.split("\n")) {
                String trimmed = line.trim();
                if (trimmed.startsWith("[METRICS]")) {
                    try {
                        String metricsJson = trimmed.substring("[METRICS]".length()).trim();
                        com.alibaba.fastjson.JSONObject metrics = com.alibaba.fastjson.JSON.parseObject(metricsJson);
                        task.setAccuracy(metrics.getBigDecimal("accuracy"));
                        task.setLoss(metrics.getBigDecimal("loss"));
                        task.setPrecisionRate(metrics.getBigDecimal("precision"));
                        task.setRecallRate(metrics.getBigDecimal("recall"));
                        task.setF1Score(metrics.getBigDecimal("f1"));
                        task.setAuc(metrics.getBigDecimal("auc"));
                        if (metrics.containsKey("confusionMatrix")) {
                            task.setConfusionMatrix(metrics.getString("confusionMatrix"));
                        }
                        metricsParsed = true;
                        log.info("已解析训练指标: acc={}, loss={}, precision={}, recall={}, f1={}, auc={}",
                                metrics.get("accuracy"), metrics.get("loss"),
                                metrics.get("precision"), metrics.get("recall"), metrics.get("f1"), metrics.get("auc"));
                    } catch (Exception e) {
                        log.warn("解析训练指标JSON失败: {}", e.getMessage());
                    }
                    break;
                }
            }

            if (!metricsParsed) {
                log.warn("未从Python输出中解析到训练指标，使用默认值");
                task.setAccuracy(java.math.BigDecimal.valueOf(0.0));
                task.setLoss(java.math.BigDecimal.valueOf(0.0));
                task.setRecallRate(java.math.BigDecimal.valueOf(0.0));
                task.setPrecisionRate(java.math.BigDecimal.valueOf(0.0));
                task.setF1Score(java.math.BigDecimal.valueOf(0.0));
                task.setAuc(java.math.BigDecimal.valueOf(0.0));
                task.setConfusionMatrix("[]");
            }
            
            trainTaskMapper.updateById(task);
            
            // 自动注册模型到模型版本表
            registerModelToVersionTable(task);
            
            log.info("训练任务完成: {}", task.getTaskName());

        } catch (Exception e) {
            // 训练失败
            task.setStatus("failed");
            task.setEndTime(new Date());
            task.setErrorMessage(e.getMessage());
            trainTaskMapper.updateById(task);
            log.error("训练任务失败: {}", task.getTaskName(), e);
        }
    }

    /**
     * 构建训练参数（train.py 期望: csv_path, model_output_path）
     */
    private String[] buildTrainingArguments(TrainTask task) {
        String projectRoot = System.getProperty("user.dir");
        
        // 获取 Python 脚本路径
        String pythonScriptPath = projectRoot + File.separator + "python" + File.separator + 
                                  (task.getPythonScript() != null ? task.getPythonScript() : "train.py");
        
        // 获取 CSV 文件完整路径
        String csvPath;
        Files trainFile = fileMapper.selectById(task.getTrainFileId());
        if (trainFile != null && trainFile.getUrl() != null) {
            String filePath = trainFile.getUrl();
            if (filePath.startsWith("/") || filePath.startsWith("\\") || filePath.contains(":")) {
                csvPath = filePath;
            } else {
                csvPath = projectRoot + File.separator + filePath;
            }
        } else {
            // 如果找不到文件记录，尝试直接使用文件名
            csvPath = projectRoot + File.separator + "data" + File.separator + "train" + File.separator + 
                      (task.getTrainFileName() != null ? task.getTrainFileName() : "");
        }
        
        String modelName = task.getModelName() != null ? task.getModelName() : "diabetes_model";
        String modelOutputPath = projectRoot + File.separator + "data" + File.separator + "models" + File.separator + modelName + ".pth";

        // 构建完整的命令行参数
        // 格式: [python, script_path, csv_path, model_output_path, --lr, --epochs, --batch-size]
        java.util.List<String> argList = new java.util.ArrayList<>();
        argList.add("python");
        argList.add(pythonScriptPath);
        argList.add(csvPath);
        argList.add(modelOutputPath);

        // 解析超参数并传递给脚本
        if (task.getHyperParams() != null && !task.getHyperParams().isEmpty()) {
            try {
                com.alibaba.fastjson.JSONObject hyperParams = com.alibaba.fastjson.JSON.parseObject(task.getHyperParams());
                if (hyperParams.containsKey("learningRate") && hyperParams.get("learningRate") != null) {
                    argList.add("--lr");
                    argList.add(hyperParams.getString("learningRate"));
                }
                if (hyperParams.containsKey("epochs") && hyperParams.get("epochs") != null) {
                    argList.add("--epochs");
                    argList.add(hyperParams.getString("epochs"));
                }
                if (hyperParams.containsKey("batchSize") && hyperParams.get("batchSize") != null) {
                    argList.add("--batch-size");
                    argList.add(hyperParams.getString("batchSize"));
                }

                // 增量训练参数
                String scriptName = task.getPythonScript() != null ? task.getPythonScript().toLowerCase() : "";
                if (scriptName.contains("incremental") && hyperParams.containsKey("incrementalParams")) {
                    com.alibaba.fastjson.JSONObject incParams = hyperParams.getJSONObject("incrementalParams");

                    if (incParams.containsKey("baseModelId") && incParams.get("baseModelId") != null) {
                        Integer baseModelId = incParams.getInteger("baseModelId");
                        ModelVersion baseModel = modelVersionMapper.selectById(baseModelId);
                        if (baseModel != null && baseModel.getFilePath() != null) {
                            argList.add("--base-model");
                            argList.add(baseModel.getFilePath());
                            log.info("增量训练基础模型: {}", baseModel.getFilePath());
                        } else {
                            throw new RuntimeException("基础模型不存在或路径为空，ID: " + baseModelId);
                        }
                    }

                    if (incParams.containsKey("reusePreprocessor") && incParams.getBooleanValue("reusePreprocessor")) {
                        argList.add("--reuse-preprocessor");
                    }

                    if (incParams.containsKey("freezeLayers") && incParams.getString("freezeLayers") != null) {
                        String freezeLayers = incParams.getString("freezeLayers");
                        if (!"none".equals(freezeLayers)) {
                            argList.add("--freeze-layers");
                            argList.add(freezeLayers);
                        }
                    }
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.warn("解析超参数失败，使用默认值: {}", e.getMessage());
            }
        }

        String[] args = argList.toArray(new String[0]);
        
        log.info("构建训练参数:");
        for (int i = 0; i < args.length; i++) {
            log.info("  参数[{}]: {}", i, args[i]);
        }
        
        return args;
    }

    /**
     * 注册模型到模型版本表
     */
    private void registerModelToVersionTable(TrainTask task) {
        try {
            // 检查是否已存在相同版本
            String version = "v" + System.currentTimeMillis() % 10000;
            
            String projectRoot = System.getProperty("user.dir");
            String modelName = task.getModelName();
            String modelsBasePath = projectRoot + File.separator + "data" + File.separator + "models";
            
            // 按分类设置路径
            String modelFilePath = modelsBasePath + File.separator + "pth_models" + File.separator + modelName + ".pth";
            String scalerPath = modelsBasePath + File.separator + "pkl_files" + File.separator + modelName + "_scaler.pkl";
            String encoderPath = modelsBasePath + File.separator + "pkl_files" + File.separator + modelName + "_encoder.pkl";
            
            ModelVersion modelVersion = new ModelVersion();
            modelVersion.setModelName(task.getModelName());
            modelVersion.setVersion(version);
            modelVersion.setSource("online_train");
            modelVersion.setFilePath(modelFilePath);
            modelVersion.setScalerPath(scalerPath);
            modelVersion.setEncoderPath(encoderPath);
            modelVersion.setDescription("通过在线训练任务创建: " + task.getTaskName());
            
            // 设置性能指标
            Map<String, Object> metrics = new HashMap<>();
            if (task.getAccuracy() != null) metrics.put("accuracy", task.getAccuracy());
            if (task.getLoss() != null) metrics.put("loss", task.getLoss());
            if (task.getRecallRate() != null) metrics.put("recallRate", task.getRecallRate());
            if (task.getPrecisionRate() != null) metrics.put("precisionRate", task.getPrecisionRate());
            if (task.getF1Score() != null) metrics.put("f1Score", task.getF1Score());
            if (task.getAuc() != null) metrics.put("auc", task.getAuc());
            if (task.getConfusionMatrix() != null) metrics.put("confusionMatrix", task.getConfusionMatrix());
            modelVersion.setMetrics(JSONUtil.toJsonStr(metrics));
            
            modelVersion.setStatus("inactive");
            modelVersion.setCreateTime(new Date());
            
            modelVersionMapper.insert(modelVersion);
            
            log.info("模型已自动注册到版本表: {}", task.getModelName());
            log.info("  模型路径: {}", modelFilePath);
            log.info("  缩放器路径: {}", scalerPath);
            log.info("  编码器路径: {}", encoderPath);
            
        } catch (Exception e) {
            log.error("模型注册失败", e);
        }
    }

    /**
     * 获取训练任务列表
     */
    public java.util.List<TrainTask> getTaskList() {
        LambdaQueryWrapper<TrainTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(TrainTask::getCreateTime);
        return trainTaskMapper.selectList(queryWrapper);
    }

    /**
     * 删除训练任务（同时删除关联的模型文件和模型版本记录）
     */
    public boolean deleteTask(Integer taskId) {
        TrainTask task = trainTaskMapper.selectById(taskId);
        if (task == null) {
            log.warn("训练任务不存在，ID: {}", taskId);
            return false;
        }
        
        String modelName = task.getModelName();
        
        // 删除关联的模型文件
        if (modelName != null && !modelName.isEmpty()) {
            deleteModelFiles(modelName);
        }
        
        // 删除关联的模型版本记录
        if (modelName != null && !modelName.isEmpty()) {
            deleteModelVersionRecords(modelName);
        }
        
        // 删除训练任务记录
        int result = trainTaskMapper.deleteById(taskId);
        if (result > 0) {
            log.info("训练任务已删除，ID: {}, 模型名称: {}", taskId, modelName);
            return true;
        } else {
            log.error("删除训练任务失败，ID: {}", taskId);
            return false;
        }
    }
    
    /**
     * 删除模型文件（.pth、_scaler.pkl、_encoder.pkl、_background.npy）
     */
    private void deleteModelFiles(String modelName) {
        String projectRoot = System.getProperty("user.dir");
        String modelsBasePath = projectRoot + File.separator + "data" + File.separator + "models";
        
        // 模型权重文件路径
        String pthPath = modelsBasePath + File.separator + "pth_models" + File.separator + modelName + ".pth";
        // 标准化器文件路径
        String scalerPath = modelsBasePath + File.separator + "pkl_files" + File.separator + modelName + "_scaler.pkl";
        // 编码器文件路径
        String encoderPath = modelsBasePath + File.separator + "pkl_files" + File.separator + modelName + "_encoder.pkl";
        // SHAP背景数据文件路径
        String backgroundPath = modelsBasePath + File.separator + "npy_data" + File.separator + modelName + "_background.npy";
        
        // 删除文件
        deleteFileIfExists(pthPath, "模型权重文件");
        deleteFileIfExists(scalerPath, "标准化器文件");
        deleteFileIfExists(encoderPath, "编码器文件");
        deleteFileIfExists(backgroundPath, "SHAP背景数据文件");
    }
    
    /**
     * 删除单个文件（如果存在）
     */
    private void deleteFileIfExists(String filePath, String fileType) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                log.info("已删除{}: {}", fileType, filePath);
            } else {
                log.error("删除{}失败: {}", fileType, filePath);
            }
        } else {
            log.debug("{}不存在: {}", fileType, filePath);
        }
    }
    
    /**
     * 删除模型版本记录（仅删除非激活状态的记录）
     */
    private void deleteModelVersionRecords(String modelName) {
        try {
            LambdaQueryWrapper<ModelVersion> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ModelVersion::getModelName, modelName)
                       .ne(ModelVersion::getStatus, "active");
            
            java.util.List<ModelVersion> versions = modelVersionMapper.selectList(queryWrapper);
            
            if (versions.isEmpty()) {
                log.info("未找到可删除的模型版本记录，模型名称: {}", modelName);
                return;
            }
            
            for (ModelVersion version : versions) {
                try {
                    modelVersionMapper.deleteById(version.getId());
                    log.info("已删除模型版本记录，ID: {}, 版本: {}", version.getId(), version.getVersion());
                } catch (Exception e) {
                    log.error("删除模型版本记录失败，ID: {}", version.getId(), e);
                }
            }
            
            log.info("已删除模型版本记录，模型名称: {}, 删除数量: {}", modelName, versions.size());
        } catch (Exception e) {
            log.error("查询模型版本记录失败，模型名称: {}", modelName, e);
        }
    }
}
