package com.oda.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@TableName(value = "user_health_profiles", autoResultMap = true)
public class HealthProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    // [原有字段]保持大写映射,严格兼容你的数据库列?
    @TableField("Pregnancies")
    private Integer Pregnancies;

    @TableField("Glucose")
    private Double Glucose;

    @TableField("BloodPressure")
    private Integer BloodPressure;

    @TableField("SkinThickness")
    private Integer SkinThickness;

    @TableField("Insulin")
    private Double Insulin;

    @TableField("BMI")
    private Double BMI;

    @TableField("DiabetesPedigreeFunction")
    private Double DiabetesPedigreeFunction;

    @TableField("Age")
    private Integer Age;

    private String symptoms;
    private String fileUrl; // 原始上传的文件名

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ✅[新增流程字段?
    private String csvFilePath;      // 生成?CSV 文件在服务器的绝对路?
    private String status;           // 状态:PENDING (待诊?, DONE (已完?
    private String diagnosisResult;  // 诊断员填写的最终结?
    private LocalDateTime diagnoseTime; // 诊断完成的时?

    // === 预测相关字段 ===
    private String riskLevel;
    private Double riskProbability;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String predictionJson;

    // === 生活方式字段 ===
    private Double height;
    private Double weight;
    private String exerciseFrequency;
    private String dietHabit;
    private String smoking;
    private String drinking;
    private String gender;

    // === AI 建议 ===
    private String aiAdvice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPregnancies() {
        return Pregnancies;
    }

    public void setPregnancies(Integer pregnancies) {
        Pregnancies = pregnancies;
    }

    public Double getGlucose() {
        return Glucose;
    }

    public void setGlucose(Double glucose) {
        Glucose = glucose;
    }

    public Integer getBloodPressure() {
        return BloodPressure;
    }

    public void setBloodPressure(Integer bloodPressure) {
        BloodPressure = bloodPressure;
    }

    public Integer getSkinThickness() {
        return SkinThickness;
    }

    public void setSkinThickness(Integer skinThickness) {
        SkinThickness = skinThickness;
    }

    public Double getInsulin() {
        return Insulin;
    }

    public void setInsulin(Double insulin) {
        Insulin = insulin;
    }

    @JsonProperty("bmi")
    public Double getBMI() {
        return BMI;
    }

    public void setBMI(Double BMI) {
        this.BMI = BMI;
    }

    public Double getDiabetesPedigreeFunction() {
        return DiabetesPedigreeFunction;
    }

    public void setDiabetesPedigreeFunction(Double diabetesPedigreeFunction) {
        DiabetesPedigreeFunction = diabetesPedigreeFunction;
    }

    public Integer getAge() {
        return Age;
    }

    public void setAge(Integer age) {
        Age = age;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiagnosisResult() {
        return diagnosisResult;
    }

    public void setDiagnosisResult(String diagnosisResult) {
        this.diagnosisResult = diagnosisResult;
    }

    public LocalDateTime getDiagnoseTime() {
        return diagnoseTime;
    }

    public void setDiagnoseTime(LocalDateTime diagnoseTime) {
        this.diagnoseTime = diagnoseTime;
    }

    // === 预测相关 getter/setter ===
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Double getRiskProbability() { return riskProbability; }
    public void setRiskProbability(Double riskProbability) { this.riskProbability = riskProbability; }

    public String getPredictionJson() { return predictionJson; }
    public void setPredictionJson(String predictionJson) { this.predictionJson = predictionJson; }

    // === 生活方式 getter/setter ===
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getExerciseFrequency() { return exerciseFrequency; }
    public void setExerciseFrequency(String exerciseFrequency) { this.exerciseFrequency = exerciseFrequency; }

    public String getDietHabit() { return dietHabit; }
    public void setDietHabit(String dietHabit) { this.dietHabit = dietHabit; }

    public String getSmoking() { return smoking; }
    public void setSmoking(String smoking) { this.smoking = smoking; }

    public String getDrinking() { return drinking; }
    public void setDrinking(String drinking) { this.drinking = drinking; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // === AI 建议 ===
    public String getAiAdvice() { return aiAdvice; }
    public void setAiAdvice(String aiAdvice) { this.aiAdvice = aiAdvice; }

    public String getCsvFileName() {

        return "";
    }

}