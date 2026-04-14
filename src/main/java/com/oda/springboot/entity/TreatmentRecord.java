package com.oda.springboot.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 诊疗档案实体类,映射数据库sys_treatment_record
 */
public class TreatmentRecord {

    @ExcelIgnore // Excel导入忽略主键
    private Long id;

    @ExcelProperty(value = "患者姓名", index = 0)
    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @ExcelProperty(value = "身份证号", index = 1)
    private String idCard;

    @ExcelProperty(value = "联系电话", index = 2)
    private String phone;

    @ExcelProperty(value = "性别", index = 3)
    private String gender;

    @ExcelProperty(value = "年龄", index = 4)
    private Integer age;

    @ExcelProperty(value = "血糖值(mmol/L)", index = 5)
    private BigDecimal bloodSugar;

    @ExcelProperty(value = "诊断结果", index = 6)
    private String diagnosisResult;

    @ExcelProperty(value = "治疗方案", index = 7)
    private String treatmentPlan;

    @ExcelProperty(value = "负责医生", index = 8)
    private String doctorName;

    @ExcelProperty(value = "备注", index = 9)
    private String remark;

    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(BigDecimal bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getDiagnosisResult() {
        return diagnosisResult;
    }

    public void setDiagnosisResult(String diagnosisResult) {
        this.diagnosisResult = diagnosisResult;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}