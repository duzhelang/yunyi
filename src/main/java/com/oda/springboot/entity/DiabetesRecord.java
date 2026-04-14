package com.oda.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

@TableName("diabetes_record") // 对应数据库表
public class DiabetesRecord {
    // 新增:主键id 字段(与数据库表id 字段对应)
    @TableId(type = IdType.AUTO) // 标记为主键,自增类型(与数据库AUTO_INCREMENT 对应)
    private Integer id;

    // 以下是你原有字段,保持不变
    private Integer pregnancies;

    private Integer glucose;

    private Integer bloodPressure;

    private Integer skinThickness;

    private Integer insulin;

    private Double bmi;

    private Double diabetesPedigreeFunction;

    private Integer age;

    private Integer outcome;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField("testfile_id")
    private Integer testfileid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPregnancies() {
        return pregnancies;
    }

    public void setPregnancies(Integer pregnancies) {
        this.pregnancies = pregnancies;
    }

    public Integer getGlucose() {
        return glucose;
    }

    public void setGlucose(Integer glucose) {
        this.glucose = glucose;
    }

    public Integer getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(Integer bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getSkinThickness() {
        return skinThickness;
    }

    public void setSkinThickness(Integer skinThickness) {
        this.skinThickness = skinThickness;
    }

    public Integer getInsulin() {
        return insulin;
    }

    public void setInsulin(Integer insulin) {
        this.insulin = insulin;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public Double getDiabetesPedigreeFunction() {
        return diabetesPedigreeFunction;
    }

    public void setDiabetesPedigreeFunction(Double diabetesPedigreeFunction) {
        this.diabetesPedigreeFunction = diabetesPedigreeFunction;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getOutcome() {
        return outcome;
    }

    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getTestfileid() {
        return testfileid;
    }

    public void setTestfileid(Integer testfileid) {
        this.testfileid = testfileid;
    }

    public void setProfileId(Long profileId) {
    }

    public void setUserId(Long userId) {
    }

    public void setRiskScore(double riskScore) {
    }

    public void setConclusion(String s) {
    }

}