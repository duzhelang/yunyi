package com.oda.springboot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyUtil {

    @Value("${files.pythonExe.path}")
    private String pythonExe;
    @Value("${files.pythonTrainMain.path}")
    private String pythonTrainMain;
    @Value("${files.pythonPredictMain.path}")
    private String pythonPredictMain;
    @Value("${files.pythonUpload.path}")
    private String pythonUpload;
    @Value("${files.pythonDataTestUpload.path}")
    private String pythonDataTestUpload;
    @Value("${files.pythonDownload.path}")
    private String pythonDownload;
    @Value("${files.JsonDownload.path}")
    private String jsonDownload;
    @Value("${server.ip}")
    private String serverIp;
    @Value("${files.pythonModelPath.path:}")
    private String pythonModelPath;

    public String getPythonExe() {
        return pythonExe;
    }

    public void setPythonExe(String pythonExe) {
        this.pythonExe = pythonExe;
    }

    public String getPythonTrainMain() {
        return pythonTrainMain;
    }

    public void setPythonTrainMain(String pythonTrainMain) {
        this.pythonTrainMain = pythonTrainMain;
    }

    public String getPythonPredictMain() {
        return pythonPredictMain;
    }

    public void setPythonPredictMain(String pythonPredictMain) {
        this.pythonPredictMain = pythonPredictMain;
    }

    public String getPythonUpload() {
        return pythonUpload;
    }

    public void setPythonUpload(String pythonUpload) {
        this.pythonUpload = pythonUpload;
    }

    public String getPythonDataTestUpload() {
        return pythonDataTestUpload;
    }

    public void setPythonDataTestUpload(String pythonDataTestUpload) {
        this.pythonDataTestUpload = pythonDataTestUpload;
    }

    public String getPythonDownload() {
        return pythonDownload;
    }

    public void setPythonDownload(String pythonDownload) {
        this.pythonDownload = pythonDownload;
    }

    public String getJsonDownload() {
        return jsonDownload;
    }

    public void setJsonDownload(String jsonDownload) {
        this.jsonDownload = jsonDownload;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getPythonModelPath() {
        return pythonModelPath;
    }

    public void setPythonModelPath(String pythonModelPath) {
        this.pythonModelPath = pythonModelPath;
    }

    // 获取 Python 脚本目录路径
    public String getPythonPath() {
        // 从 pythonTrainMain 路径中提取 python 目录路径
        if (pythonTrainMain != null) {
            // 处理 Windows 路径分隔符
            String path = pythonTrainMain.replace("\\", "/");
            if (path.contains("/python/")) {
                String pythonPath = pythonTrainMain.substring(0, pythonTrainMain.lastIndexOf("/python/") + "/python/".length());
                // 转换为系统默认路径分隔符
                return pythonPath.replace("/", System.getProperty("file.separator"));
            }
        }
        // 默认路径
        return System.getProperty("user.dir") + System.getProperty("file.separator") + "python" + System.getProperty("file.separator");
    }

} 
