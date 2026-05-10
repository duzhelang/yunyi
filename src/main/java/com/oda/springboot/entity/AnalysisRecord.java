package com.oda.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.time.LocalDateTime;

/**
 * 分析记录实体类
 * 用于存储群体分析和个体洞察的分析结果
 */
@TableName(value = "analysis_record", autoResultMap = true)
public class AnalysisRecord {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分析类型：group/individual */
    private String analysisType;

    /** 操作用户ID */
    private Long userId;

    /** 分析的档案ID列表(JSON数组) */
    private String profileIds;

    /** 分析结果数据 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String resultData;

    /** 分析时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime analysisTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProfileIds() {
        return profileIds;
    }

    public void setProfileIds(String profileIds) {
        this.profileIds = profileIds;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public LocalDateTime getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(LocalDateTime analysisTime) {
        this.analysisTime = analysisTime;
    }
}
