package com.cucn.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("diabetes_video")
public class DiabetesVideo {
    private Long id;
    private String type; // 对应糖尿病类?
    private String videoTitle; // 视频标题
    private String videoUrl; // 视频存储路径或在线地址
    private String coverUrl; // 封面图路?
    private String description; // 视频描述
}