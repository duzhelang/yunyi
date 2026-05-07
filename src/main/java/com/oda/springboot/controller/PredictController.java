package com.oda.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.oda.springboot.common.Result;
import com.oda.springboot.utils.PropertyUtil;
import com.oda.springboot.utils.UsePythonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/predict")
@CrossOrigin
public class PredictController {

    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * 执行批量预测
     * @param params 预测参数
     * @return 预测结果
     */
    @PostMapping("/batch")
    public Result<Map<String, Object>> batchPredict(@RequestBody Map<String, Object> params) {
        try {
            // 1. 获取参数
            String testFilePath = (String) params.get("testFilePath");
            String modelPath = (String) params.get("modelPath");
            String title = (String) params.get("title");
            String jsonName = (String) params.get("jsonName");

            // 2. 参数校验
            if (StrUtil.isBlank(testFilePath)) {
                return Result.error("400", "请选择测试数据文件");
            }
            if (StrUtil.isBlank(modelPath)) {
                return Result.error("400", "请选择预测模型");
            }

            // 3. 构建完整文件路径
            String projectRoot = System.getProperty("user.dir");
            
            // 处理测试文件路径
            File testFile;
            if (testFilePath.startsWith("/") || testFilePath.startsWith("\\") || testFilePath.contains(":")) {
                // 绝对路径
                testFile = new File(testFilePath);
            } else {
                // 相对路径，从data/test目录查找
                testFile = new File(projectRoot, testFilePath);
            }
            
            if (!testFile.exists()) {
                // 尝试在data/test目录下查找
                testFile = new File(projectRoot, "data" + File.separator + "test" + File.separator + testFilePath);
            }
            
            if (!testFile.exists()) {
                return Result.error("404", "测试文件不存在: " + testFilePath);
            }

            // 处理模型路径
            File modelFile;
            if (modelPath.startsWith("/") || modelPath.startsWith("\\") || modelPath.contains(":")) {
                modelFile = new File(modelPath);
            } else {
                modelFile = new File(projectRoot, modelPath);
            }
            
            // 如果模型文件不存在，尝试在data/models目录下查找
            if (!modelFile.exists()) {
                String modelsBasePath = projectRoot + File.separator + "data" + File.separator + "models";
                String modelName = modelPath;
                
                // 如果传入的是模型名称（不含路径），构建完整路径
                if (!modelPath.contains(File.separator) && !modelPath.contains("/")) {
                    modelName = modelPath.replace(".pth", "");
                    modelFile = new File(modelsBasePath + File.separator + "pth_models" + File.separator + modelName + ".pth");
                }
            }
            
            if (!modelFile.exists()) {
                return Result.error("404", "模型文件不存在: " + modelPath);
            }

            // 4. 准备输出参数
            if (StrUtil.isBlank(title)) {
                title = "批量预测_" + System.currentTimeMillis();
            }
            if (StrUtil.isBlank(jsonName)) {
                jsonName = UUID.randomUUID().toString() + ".json";
            } else if (!jsonName.endsWith(".json")) {
                jsonName = jsonName + ".json";
            }

            // 5. 确保输出目录存在
            String jsonDir = propertyUtil.getJsonDownload();
            if (StrUtil.isBlank(jsonDir)) {
                jsonDir = projectRoot + File.separator + "data" + File.separator + "json";
            }
            File jsonDirFile = new File(jsonDir);
            if (!jsonDirFile.exists()) {
                jsonDirFile.mkdirs();
            }

            // 6. 调用Python预测脚本
            log.info("开始执行批量预测...");
            log.info("测试文件: {}", testFile.getAbsolutePath());
            log.info("模型文件: {}", modelFile.getAbsolutePath());
            log.info("输出文件: {}", jsonDir + File.separator + jsonName);

            // 构建Python脚本完整路径
            String pythonScriptPath = propertyUtil.getPythonPredictMain();
            if (pythonScriptPath.startsWith("/") || pythonScriptPath.startsWith("\\")) {
                pythonScriptPath = projectRoot + pythonScriptPath;
            } else {
                pythonScriptPath = projectRoot + File.separator + pythonScriptPath;
            }

            int exitCode = UsePythonUtils.callPython(new String[]{
                    propertyUtil.getPythonExe(),
                    pythonScriptPath,
                    testFile.getAbsolutePath(),
                    jsonName,
                    title,
                    modelFile.getAbsolutePath()
            });

            if (exitCode != 0) {
                log.error("预测脚本执行失败，退出码: {}", exitCode);
                return Result.error("500", "预测执行失败，退出码: " + exitCode);
            }

            // 7. 检查输出文件
            File outputFile = new File(jsonDir, jsonName);
            if (!outputFile.exists()) {
                // 尝试在项目根目录查找
                outputFile = new File(projectRoot, "data" + File.separator + "json" + File.separator + jsonName);
            }
            
            if (!outputFile.exists()) {
                log.warn("预测完成但未找到输出文件，可能在其他位置");
                // 返回成功，但提示文件位置不确定
                Map<String, Object> result = new HashMap<>();
                result.put("status", "success");
                result.put("message", "预测执行完成，请在data/json目录查看结果");
                result.put("jsonName", jsonName);
                return Result.success(result);
            }

            // 8. 返回成功结果
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "预测完成");
            result.put("jsonName", jsonName);
            result.put("outputPath", outputFile.getAbsolutePath());
            result.put("fileSize", outputFile.length());
            
            log.info("批量预测完成，输出文件: {}", outputFile.getAbsolutePath());
            return Result.success(result);

        } catch (Exception e) {
            log.error("批量预测执行异常", e);
            return Result.error("500", "预测执行异常: " + e.getMessage());
        }
    }

    /**
     * 获取预测结果文件列表
     */
    @GetMapping("/results")
    public Result<Map<String, Object>> getPredictResults() {
        try {
            String projectRoot = System.getProperty("user.dir");
            String jsonDir = propertyUtil.getJsonDownload();
            if (StrUtil.isBlank(jsonDir)) {
                jsonDir = projectRoot + File.separator + "data" + File.separator + "json";
            }
            
            File dir = new File(jsonDir);
            if (!dir.exists()) {
                dir.mkdirs();
                return Result.success(Map.of("files", new String[0]));
            }
            
            File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (jsonFiles == null) {
                return Result.success(Map.of("files", new String[0]));
            }
            
            String[] fileNames = new String[jsonFiles.length];
            long[] fileSizes = new long[jsonFiles.length];
            long[] fileTimes = new long[jsonFiles.length];
            
            for (int i = 0; i < jsonFiles.length; i++) {
                fileNames[i] = jsonFiles[i].getName();
                fileSizes[i] = jsonFiles[i].length();
                fileTimes[i] = jsonFiles[i].lastModified();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("files", fileNames);
            result.put("sizes", fileSizes);
            result.put("times", fileTimes);
            result.put("directory", jsonDir);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取预测结果列表失败", e);
            return Result.error("500", "获取结果列表失败: " + e.getMessage());
        }
    }

    /**
     * 读取预测结果文件内容
     */
    @GetMapping("/result/{fileName}")
    public Result<Map<String, Object>> getPredictResult(@PathVariable String fileName) {
        try {
            String projectRoot = System.getProperty("user.dir");
            String jsonDir = propertyUtil.getJsonDownload();
            if (StrUtil.isBlank(jsonDir)) {
                jsonDir = projectRoot + File.separator + "data" + File.separator + "json";
            }
            
            File jsonFile = new File(jsonDir, fileName);
            if (!jsonFile.exists()) {
                // 尝试其他可能的路径
                jsonFile = new File(projectRoot, "data" + File.separator + "json" + File.separator + fileName);
            }
            
            if (!jsonFile.exists()) {
                return Result.error("404", "结果文件不存在: " + fileName);
            }
            
            String content = FileUtil.readUtf8String(jsonFile);
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("content", content);
            result.put("size", jsonFile.length());
            result.put("lastModified", jsonFile.lastModified());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("读取预测结果文件失败", e);
            return Result.error("500", "读取结果失败: " + e.getMessage());
        }
    }
}
