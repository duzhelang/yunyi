package com.oda.springboot.controller;

import com.oda.springboot.common.Result;
import com.oda.springboot.service.PythonScriptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/python-scripts")
@Slf4j
@CrossOrigin
public class PythonScriptController {

    @Autowired
    private PythonScriptService pythonScriptService;

    /**
     * 获取所有训练脚本列表
     */
    @GetMapping("/training")
    public Result<List<Map<String, Object>>> getTrainingScripts() {
        try {
            List<Map<String, Object>> scripts = pythonScriptService.getTrainingScripts();
            return Result.success(scripts);
        } catch (Exception e) {
            log.error("获取训练脚本列表失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
}
