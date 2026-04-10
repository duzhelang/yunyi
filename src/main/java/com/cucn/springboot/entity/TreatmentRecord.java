package com.cucn.springboot.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 诊疗档案实体类,映射数据库sys_treatment_record
 */
@Data
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
}