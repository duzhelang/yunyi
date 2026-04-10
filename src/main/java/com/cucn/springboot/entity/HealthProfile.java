package com.cucn.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_health_profiles")
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

    public String getCsvFileName() {

        return "";
    }
}