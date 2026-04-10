package com.cucn.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cucn.springboot.common.Result;
import com.cucn.springboot.entity.DiabetesVideo;
import com.cucn.springboot.service.IDiabetesVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diabetesVideo")
public class DiabetesVideoController {

    @Autowired
    private IDiabetesVideoService diabetesVideoService;

    // 获取所有糖尿病视频
    @GetMapping("/list")
    public Result<List<DiabetesVideo>> getAllVideos() {
        List<DiabetesVideo> list = diabetesVideoService.list();
        return Result.success(list);
    }

    @GetMapping("/listByType")
    public Result<List<DiabetesVideo>> getVideosByType(@RequestParam String type) {
        LambdaQueryWrapper<DiabetesVideo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiabetesVideo::getType, type);
        List<DiabetesVideo> list = diabetesVideoService.list(wrapper);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> addVideo(@RequestBody DiabetesVideo diabetesVideo) {
        diabetesVideoService.save(diabetesVideo);
        return Result.success("添加成功");
    }
}