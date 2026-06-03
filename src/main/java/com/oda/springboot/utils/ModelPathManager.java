package com.oda.springboot.utils;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 模型相关路径集中管理
 */
@Component
public class ModelPathManager {

    private final String projectRoot;
    private final String modelsBasePath;

    public ModelPathManager() {
        this.projectRoot = System.getProperty("user.dir");
        this.modelsBasePath = projectRoot + File.separator + "data" + File.separator + "models";
    }

    public String getProjectRoot() {
        return projectRoot;
    }

    public String getModelsBasePath() {
        return modelsBasePath;
    }

    public String getModelFilePath(String modelName) {
        return modelsBasePath + File.separator + "pth_models" + File.separator + modelName + ".pth";
    }

    public String getScalerPath(String modelName) {
        return modelsBasePath + File.separator + "pkl_files" + File.separator + modelName + "_scaler.pkl";
    }

    public String getEncoderPath(String modelName) {
        return modelsBasePath + File.separator + "pkl_files" + File.separator + modelName + "_encoder.pkl";
    }

    public String getBackgroundPath(String modelName) {
        return modelsBasePath + File.separator + "npy_data" + File.separator + modelName + "_background.npy";
    }

    public String getPythonScriptPath(String scriptName) {
        return projectRoot + File.separator + "python" + File.separator + scriptName;
    }

    public String getCsvPath(String fileName) {
        return projectRoot + File.separator + "data" + File.separator + "train" + File.separator + fileName;
    }
}
