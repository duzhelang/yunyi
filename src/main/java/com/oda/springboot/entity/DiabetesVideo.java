package com.oda.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("diabetes_video")
public class DiabetesVideo {
    private Long id;
    private String type; // 对应糖尿病类?
    private String videoTitle; // 视频标题
    private String videoUrl; // 视频存储路径或在线地址
    private String coverUrl; // 封面图路?
    private String description; // 视频描述

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}