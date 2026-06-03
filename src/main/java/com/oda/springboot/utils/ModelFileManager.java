package com.oda.springboot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 模型文件操作集中管理（删除、校验等）
 */
@Component
public class ModelFileManager {

    private static final Logger log = LoggerFactory.getLogger(ModelFileManager.class);

    private final ModelPathManager pathManager;

    public ModelFileManager(ModelPathManager pathManager) {
        this.pathManager = pathManager;
    }

    /**
     * 删除模型相关的所有文件（.pth、_scaler.pkl、_encoder.pkl、_background.npy）
     */
    public void deleteModelFiles(String modelName) {
        log.info("[文件删除] 开始删除模型文件, 模型名称: {}", modelName);

        String[] filePaths = {
                pathManager.getModelFilePath(modelName),
                pathManager.getScalerPath(modelName),
                pathManager.getEncoderPath(modelName),
                pathManager.getBackgroundPath(modelName)
        };
        String[] fileTypes = {
                "模型权重文件", "标准化器文件", "编码器文件", "SHAP背景数据文件"
        };

        boolean allSuccess = true;
        for (int i = 0; i < filePaths.length; i++) {
            if (!deleteFileIfExists(filePaths[i], fileTypes[i])) {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            log.info("[文件删除] 所有文件处理完成, 模型名称: {}", modelName);
        } else {
            log.warn("[文件删除] 部分文件删除失败, 模型名称: {}", modelName);
        }
    }

    /**
     * 校验模型所需的核心文件是否都存在
     */
    public boolean validateModelFiles(String modelName) {
        String[] requiredFiles = {
                pathManager.getModelFilePath(modelName),
                pathManager.getScalerPath(modelName),
                pathManager.getEncoderPath(modelName)
        };
        for (String filePath : requiredFiles) {
            if (!new File(filePath).exists()) {
                log.warn("[文件验证] 模型文件缺失, 路径: {}", filePath);
                return false;
            }
        }
        return true;
    }

    private boolean deleteFileIfExists(String filePath, String fileType) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.debug("[文件删除] 文件不存在, 跳过: {}, 路径: {}", fileType, filePath);
            return true;
        }
        if (file.delete()) {
            log.debug("[文件删除] 删除成功, 文件类型: {}, 路径: {}", fileType, filePath);
            return true;
        } else {
            log.warn("[文件删除] 删除失败, 文件类型: {}, 路径: {}", fileType, filePath);
            return false;
        }
    }
}
