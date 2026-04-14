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

}
