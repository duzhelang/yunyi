package com.oda.springboot.service;

import cn.hutool.log.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PythonScriptService {

    private static final Log log = Log.get();

    /**
     * 获取 python/ 目录下的训练脚本文件
     * @return
     */
    public List<Map<String, Object>> getTrainingScripts() {
        List<Map<String, Object>> scripts = new ArrayList<>();

        String projectRoot = System.getProperty("user.dir");
        String pythonDir = projectRoot + File.separator + "python";

        File dir = new File(pythonDir);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("Python目录不存在: {}", pythonDir);
            return scripts;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return scripts;
        }

        for (File file : files) {
            try {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".py")) {
                    String fileName = file.getName();

                    // 检查文件是否是训练相关的脚本
                    if (isTrainingScript(fileName)) {
                        Map<String, Object> script = new HashMap<>();
                        script.put("name", fileName);
                        script.put("path", file.getAbsolutePath());
                        script.put("relativePath", "python" + File.separator + fileName);
                        script.put("description", getScriptDescription(fileName));
                        scripts.add(script);
                    }
                }
            } catch (Exception e) {
                log.error("处理Python脚本时出错: {}", file.getName(), e);
            }
        }

        log.info("找到 {} 个训练脚本", scripts.size());
        return scripts;
    }

    /**
     * 判断是否是训练脚本
     * @param fileName
     * @return
     */
    private boolean isTrainingScript(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.contains("train") ||
               lowerName.contains("model") ||
               lowerName.contains("A10") ||
               lowerName.equals("train.py");
    }

    /**
     * 获取脚本描述
     * @param fileName
     * @return
     */
    private String getScriptDescription(String fileName) {
        if ("train.py".equalsIgnoreCase(fileName)) {
            return "最新的 PyTorch 神经网络训练脚本";
        } else if (fileName.toLowerCase().contains("A10")) {
            return "旧版整合训练脚本（已弃用）";
        } else if (fileName.toLowerCase().contains("model")) {
            return "模型训练相关脚本";
        } else {
            return "Python 训练脚本";
        }
    }
}
