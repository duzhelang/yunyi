package com.oda.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.time.LocalDateTime;

@TableName(value = "prediction_record", autoResultMap = true)
public class PredictionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long healthProfileId;

    private Double probability;

    private String riskLevel;

    private String confidenceInterval;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private String featureImportance;

    private Integer age;

    private String gender;

    private LocalDateTime predictedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHealthProfileId() { return healthProfileId; }
    public void setHealthProfileId(Long healthProfileId) { this.healthProfileId = healthProfileId; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getConfidenceInterval() { return confidenceInterval; }
    public void setConfidenceInterval(String confidenceInterval) { this.confidenceInterval = confidenceInterval; }

    public String getFeatureImportance() { return featureImportance; }
    public void setFeatureImportance(String featureImportance) { this.featureImportance = featureImportance; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDateTime getPredictedAt() { return predictedAt; }
    public void setPredictedAt(LocalDateTime predictedAt) { this.predictedAt = predictedAt; }
}
