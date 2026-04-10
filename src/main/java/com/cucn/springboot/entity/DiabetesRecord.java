package com.cucn.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
@TableName("diabetes_record") // 对应数据库表
public class DiabetesRecord {
    // 新增:主键id 字段(与数据库表id 字段对应)
    @TableId(type = IdType.AUTO) // 标记为主键,自增类型(与数据库AUTO_INCREMENT 对应)
    @ApiModelProperty("主键ID")
    private Integer id;

    // 以下是你原有字段,保持不变
    @ApiModelProperty("怀孕次数")
    private Integer pregnancies;

    @ApiModelProperty("葡萄糖浓度(血糖值)")
    private Integer glucose;

    @ApiModelProperty("舒张压(mmHg)")
    private Integer bloodPressure;

    @ApiModelProperty("肱三头肌皮褶厚度(mm)")
    private Integer skinThickness;

    @ApiModelProperty("胰岛素水平(μU/ml)")
    private Integer insulin;

    @ApiModelProperty("体重指数")
    private Double bmi;

    @ApiModelProperty("糖尿病家族史系数")
    private Double diabetesPedigreeFunction;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("预测结果0=未患病,1=患病")
    private Integer outcome;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField("testfile_id")
    private Integer testfileid;

    public void setProfileId(Long profileId) {
    }

    public void setUserId(Long userId) {
    }

    public void setRiskScore(double riskScore) {
    }

    public void setConclusion(String s) {
    }
}