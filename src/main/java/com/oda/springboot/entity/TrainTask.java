package com.oda.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

@TableName("sys_train_task")
public class TrainTask {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String taskName;

    private Integer trainFileId;

    private String trainFileName;

    private String modelName;

    private String hyperParams;

    private String status;

    private BigDecimal accuracy;

    private BigDecimal loss;

    private BigDecimal recallRate;

    private BigDecimal precisionRate;

    private BigDecimal f1Score;

    private String logPath;

    private String modelOutputPath;

    private String errorMessage;

    private Date createTime;

    private Date startTime;

    private Date endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTrainFileId() {
        return trainFileId;
    }

    public void setTrainFileId(Integer trainFileId) {
        this.trainFileId = trainFileId;
    }

    public String getTrainFileName() {
        return trainFileName;
    }

    public void setTrainFileName(String trainFileName) {
        this.trainFileName = trainFileName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getHyperParams() {
        return hyperParams;
    }

    public void setHyperParams(String hyperParams) {
        this.hyperParams = hyperParams;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }

    public BigDecimal getLoss() {
        return loss;
    }

    public void setLoss(BigDecimal loss) {
        this.loss = loss;
    }

    public BigDecimal getRecallRate() {
        return recallRate;
    }

    public void setRecallRate(BigDecimal recallRate) {
        this.recallRate = recallRate;
    }

    public BigDecimal getPrecisionRate() {
        return precisionRate;
    }

    public void setPrecisionRate(BigDecimal precisionRate) {
        this.precisionRate = precisionRate;
    }

    public BigDecimal getF1Score() {
        return f1Score;
    }

    public void setF1Score(BigDecimal f1Score) {
        this.f1Score = f1Score;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getModelOutputPath() {
        return modelOutputPath;
    }

    public void setModelOutputPath(String modelOutputPath) {
        this.modelOutputPath = modelOutputPath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
