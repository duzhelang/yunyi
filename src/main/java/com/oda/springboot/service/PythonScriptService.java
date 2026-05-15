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

    private boolean isTrainingScript(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.contains("train") || lowerName.contains("model") || lowerName.contains("a10")
                || lowerName.contains("incremental") || lowerName.contains("fine_tune");
    }

    private String getScriptDescription(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.contains("incremental") || lowerName.contains("fine_tune")) {
            return "增量训练脚本（基于已有模型微调）";
        }
        if (lowerName.equals("train.py")) {
            return "最新 PyTorch 神经网络训练脚本";
        }
        if (lowerName.contains("a10")) {
            return "旧版整合训练脚本（已弃用）";
        }
        if (lowerName.contains("model")) {
            return "模型训练相关脚本";
        }
        return "Python 训练脚本";
    }
}
