package com.oda.springboot.controller;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.TrainTask;
import com.oda.springboot.service.TrainTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/train-task")
@CrossOrigin
public class TrainTaskController {

    private static final Log log = Log.get();

    @Autowired
    private TrainTaskService trainTaskService;

    /**
     * 分页查询训练任务列表
     */
    @GetMapping("/list")
    public Result<Page<TrainTask>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        
        LambdaQueryWrapper<TrainTask> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq(TrainTask::getStatus, status);
        }
        
        queryWrapper.orderByDesc(TrainTask::getCreateTime);
        
        Page<TrainTask> page = new Page<>(pageNum, pageSize);
        Page<TrainTask> result = trainTaskService.page(page, queryWrapper);
        
        return Result.success(result);
    }

    /**
     * 获取单个训练任务详情
     */
    @GetMapping("/{id}")
    public Result<TrainTask> getDetail(@PathVariable Integer id) {
        TrainTask task = trainTaskService.getById(id);
        if (task == null) {
            return Result.error("训练任务不存在");
        }
        return Result.success(task);
    }

    /**
     * 创建并启动训练任务
     */
    @PostMapping("/start")
    public Result<TrainTask> startTask(@RequestBody Map<String, Object> params) {
        try {
            TrainTask task = trainTaskService.createAndStartTask(params);
            return Result.success(task);
        } catch (Exception e) {
            log.error("启动训练任务失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除训练任务
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Integer id) {
        try {
            boolean success = trainTaskService.deleteTask(id);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除训练任务失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重试失败的训练任务
     */
    @PostMapping("/{id}/retry")
    public Result<TrainTask> retryTask(@PathVariable Integer id) {
        try {
            TrainTask task = trainTaskService.getById(id);
            if (task == null) {
                return Result.error("训练任务不存在");
            }
            
            if (!"failed".equals(task.getStatus())) {
                return Result.error("只能重试失败的任务");
            }
            
            // 重新启动任务
            Map<String, Object> params = Map.of(
                "trainFileId", task.getTrainFileId(),
                "modelName", task.getModelName(),
                "hyperParams", JSONUtil.parseObj(task.getHyperParams())
            );
            
            TrainTask newTask = trainTaskService.createAndStartTask(params);
            return Result.success(newTask);
            
        } catch (Exception e) {
            log.error("重试训练任务失败", e);
            return Result.error(e.getMessage());
        }
    }
}
