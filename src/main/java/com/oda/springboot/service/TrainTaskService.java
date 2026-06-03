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
import com.oda.springboot.exception.TrainingServiceException;
import com.oda.springboot.utils.ModelFileManager;
import com.oda.springboot.utils.ModelPathManager;
import com.oda.springboot.utils.UsePythonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ModelPathManager pathManager;

    @Autowired
    private ModelFileManager fileManager;

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
                    throw new TrainingServiceException("TRAINING_DATA_ERROR", "无效的训练文件ID: " + trainFileIdStr);
                }
            }
        } else if (trainFileIdObj instanceof Integer) {
            trainFileId = (Integer) trainFileIdObj;
        }

        // 如果是数据库ID，从数据库获取文件信息
        if (trainFileId != null) {
            Files trainFile = fileMapper.selectById(trainFileId);
            if (trainFile == null) {
                throw new TrainingServiceException("TRAINING_DATA_ERROR", "训练文件不存在, ID: " + trainFileId);
            }
            trainFileName = trainFile.getName();
        }

        if (trainFileName == null || trainFileName.isEmpty()) {
            throw new TrainingServiceException("TRAINING_DATA_ERROR", "请选择训练数据集");
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
            log.info("[训练任务] 创建成功, 任务ID: {}, 任务名称: {}", task.getId(), task.getTaskName());
            executeTrainingAsync(task);
            return task;
        } else {
            throw new TrainingServiceException("创建训练任务失败");
        }
    }

    /**
     * 异步执行训练任务
     */
    @Async
    public void executeTrainingAsync(TrainTask task) {
        long startTime = System.currentTimeMillis();
        try {
            task.setStatus("running");
            task.setStartTime(new Date());
            trainTaskMapper.updateById(task);
            log.info("[训练任务] 开始执行, 任务ID: {}, 任务名称: {}, 脚本: {}", task.getId(), task.getTaskName(), task.getPythonScript());

            String processId = UUID.randomUUID().toString();
            String[] arguments = buildTrainingArguments(task);

            final StringBuilder outputCapture = new StringBuilder();
            usePythonUtils.callPythonWithCallback(processId, arguments, (line, isError) -> {
                outputCapture.append(line).append("\n");
                log.debug("[训练日志] {}", line);
            });

            task.setStatus("completed");
            task.setEndTime(new Date());

            String modelPath = pathManager.getModelFilePath(task.getModelName());
            task.setModelOutputPath(modelPath);

            // 解析 Python 输出中的训练指标
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
                        log.info("[训练任务] 指标解析成功, acc={}, loss={}, f1={}, auc={}",
                                metrics.get("accuracy"), metrics.get("loss"), metrics.get("f1"), metrics.get("auc"));
                    } catch (Exception e) {
                        log.warn("[训练任务] 解析训练指标JSON失败: {}", e.getMessage());
                    }
                    break;
                }
            }

            if (!metricsParsed) {
                log.warn("[训练任务] 未解析到训练指标, 使用默认值, 任务ID: {}", task.getId());
                task.setAccuracy(java.math.BigDecimal.valueOf(0.0));
                task.setLoss(java.math.BigDecimal.valueOf(0.0));
                task.setRecallRate(java.math.BigDecimal.valueOf(0.0));
                task.setPrecisionRate(java.math.BigDecimal.valueOf(0.0));
                task.setF1Score(java.math.BigDecimal.valueOf(0.0));
                task.setAuc(java.math.BigDecimal.valueOf(0.0));
                task.setConfusionMatrix("[]");
            }

            trainTaskMapper.updateById(task);

            registerModelToVersionTable(task);

            long duration = System.currentTimeMillis() - startTime;
            log.info("[训练任务] 执行成功, 任务ID: {}, 任务名称: {}, 耗时: {}ms",
                    task.getId(), task.getTaskName(), duration);

        } catch (Exception e) {
            task.setStatus("failed");
            task.setEndTime(new Date());
            task.setErrorMessage(e.getMessage());
            trainTaskMapper.updateById(task);
            log.error("[训练任务] 执行失败, 任务ID: {}, 任务名称: {}, 原因: {}",
                    task.getId(), task.getTaskName(), e.getMessage(), e);
        }
    }

    /**
     * 构建训练参数（train.py 期望: csv_path, model_output_path）
     */
    private String[] buildTrainingArguments(TrainTask task) {
        String pythonScriptPath = pathManager.getPythonScriptPath(
                task.getPythonScript() != null ? task.getPythonScript() : "train.py");

        String csvPath;
        Files trainFile = fileMapper.selectById(task.getTrainFileId());
        if (trainFile != null && trainFile.getUrl() != null) {
            String filePath = trainFile.getUrl();
            if (filePath.startsWith("/") || filePath.startsWith("\\") || filePath.contains(":")) {
                csvPath = filePath;
            } else {
                csvPath = pathManager.getProjectRoot() + File.separator + filePath;
            }
        } else {
            csvPath = pathManager.getCsvPath(task.getTrainFileName() != null ? task.getTrainFileName() : "");
        }

        String modelName = task.getModelName() != null ? task.getModelName() : "diabetes_model";
        String modelOutputPath = pathManager.getModelsBasePath() + File.separator + modelName + ".pth";

        java.util.List<String> argList = new java.util.ArrayList<>();
        argList.add("python");
        argList.add(pythonScriptPath);
        argList.add(csvPath);
        argList.add(modelOutputPath);

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
                            log.info("[增量训练] 基础模型: {}", baseModel.getFilePath());
                        } else {
                            throw new TrainingServiceException("TRAINING_DATA_ERROR", "基础模型不存在或路径为空, ID: " + baseModelId);
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
            } catch (TrainingServiceException e) {
                throw e;
            } catch (Exception e) {
                log.warn("[训练参数] 解析超参数失败, 使用默认值: {}", e.getMessage());
            }
        }

        String[] args = argList.toArray(new String[0]);
        log.debug("[训练参数] 构建完成, 参数数量: {}", args.length);

        return args;
    }

    /**
     * 注册模型到模型版本表
     */
    private void registerModelToVersionTable(TrainTask task) {
        try {
            String version = "v" + System.currentTimeMillis() % 10000;
            String modelName = task.getModelName();

            String modelFilePath = pathManager.getModelFilePath(modelName);
            String scalerPath = pathManager.getScalerPath(modelName);
            String encoderPath = pathManager.getEncoderPath(modelName);

            ModelVersion modelVersion = new ModelVersion();
            modelVersion.setModelName(modelName);
            modelVersion.setVersion(version);
            modelVersion.setSource("online_train");
            modelVersion.setFilePath(modelFilePath);
            modelVersion.setScalerPath(scalerPath);
            modelVersion.setEncoderPath(encoderPath);
            modelVersion.setDescription("通过在线训练任务创建: " + task.getTaskName());

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

            log.info("[模型注册] 注册成功, 模型名称: {}, 版本: {}", modelName, version);

        } catch (Exception e) {
            log.warn("[模型注册] 注册失败(不影响训练任务), 模型名称: {}, 错误: {}",
                    task.getModelName(), e.getMessage(), e);
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
     * 文件操作在事务外执行，数据库操作在事务内执行
     */
    public boolean deleteTask(Integer taskId) {
        TrainTask task = trainTaskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[任务删除] 任务不存在, ID: {}", taskId);
            return false;
        }

        String modelName = task.getModelName();
        log.info("[任务删除] 开始删除, 任务ID: {}, 模型名称: {}, 任务名称: {}",
                taskId, modelName, task.getTaskName());

        // 文件操作在事务外执行（文件系统不支持事务回滚）
        if (modelName != null && !modelName.isEmpty()) {
            fileManager.deleteModelFiles(modelName);
        }

        // 数据库操作在事务内执行
        deleteTaskRecordsInTransaction(taskId, modelName);
        log.info("[任务删除] 删除完成, 任务ID: {}, 模型名称: {}", taskId, modelName);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    private void deleteTaskRecordsInTransaction(Integer taskId, String modelName) {
        if (modelName != null && !modelName.isEmpty()) {
            deleteModelVersionRecords(modelName);
        }
        trainTaskMapper.deleteById(taskId);
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
                log.debug("[模型版本] 无待删除记录, 模型名称: {}", modelName);
                return;
            }

            for (ModelVersion version : versions) {
                try {
                    modelVersionMapper.deleteById(version.getId());
                    log.debug("[模型版本] 已删除, ID: {}, 版本: {}", version.getId(), version.getVersion());
                } catch (Exception e) {
                    log.warn("[模型版本] 删除失败, ID: {}", version.getId(), e);
                }
            }

            log.info("[模型版本] 删除完成, 模型名称: {}, 删除数量: {}", modelName, versions.size());
        } catch (Exception e) {
            log.warn("[模型版本] 查询失败, 模型名称: {}", modelName, e);
        }
    }
}
