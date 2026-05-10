package com.oda.springboot.controller;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.ModelVersion;
import com.oda.springboot.service.ModelVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/model")
@CrossOrigin
public class ModelVersionController {

    private static final Log log = Log.get();

    @Autowired
    private ModelVersionService modelVersionService;

    /**
     * 分页查询模型列表
     */
    @GetMapping("/list")
    public Result<Page<ModelVersion>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String modelName) {
        
        LambdaQueryWrapper<ModelVersion> queryWrapper = new LambdaQueryWrapper<>();
        
        if (modelName != null && !modelName.trim().isEmpty()) {
            queryWrapper.like(ModelVersion::getModelName, modelName);
        }
        
        queryWrapper.orderByDesc(ModelVersion::getCreateTime);
        
        Page<ModelVersion> page = new Page<>(pageNum, pageSize);
        Page<ModelVersion> result = modelVersionService.page(page, queryWrapper);
        
        return Result.success(result);
    }

    /**
     * 获取模型详情
     */
    @GetMapping("/{id}")
    public Result<ModelVersion> getDetail(@PathVariable Integer id) {
        ModelVersion model = modelVersionService.getModelById(id);
        if (model == null) {
            return Result.error("模型不存在");
        }
        return Result.success(model);
    }

    /**
     * 获取当前激活的模型
     */
    @GetMapping("/active")
    public Result<ModelVersion> getActiveModel() {
        ModelVersion activeModel = modelVersionService.getActiveModel();
        if (activeModel == null) {
            return Result.error("没有激活的模型");
        }
        return Result.success(activeModel);
    }

    /**
     * 获取激活模型的调用信息（供其他模块调用）
     */
    @GetMapping("/active/info")
    public Result<Map<String, Object>> getActiveModelInfo() {
        Map<String, Object> result = modelVersionService.getActiveModelInfo();
        if ("success".equals(result.get("status"))) {
            return Result.success(result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }

    /**
     * 激活指定模型
     */
    @PostMapping("/{id}/activate")
    public Result<Void> activateModel(@PathVariable Integer id) {
        try {
            boolean success = modelVersionService.activateModel(id);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("激活失败");
            }
        } catch (Exception e) {
            log.error("激活模型失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消激活指定模型
     */
    @PostMapping("/{id}/deactivate")
    public Result<Void> deactivateModel(@PathVariable Integer id) {
        try {
            boolean success = modelVersionService.deactivateModel(id);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("取消激活失败");
            }
        } catch (Exception e) {
            log.error("取消激活模型失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除模型
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteModel(@PathVariable Integer id) {
        try {
            boolean success = modelVersionService.deleteModel(id);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除模型失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增模型
     */
    @PostMapping("/add")
    public Result<ModelVersion> addModel(@RequestBody ModelVersion modelVersion) {
        try {
            boolean success = modelVersionService.addModel(modelVersion);
            if (success) {
                return Result.success(modelVersion);
            } else {
                return Result.error("新增失败");
            }
        } catch (Exception e) {
            log.error("新增模型失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新模型信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateModel(@PathVariable Integer id, @RequestBody ModelVersion modelVersion) {
        try {
            modelVersion.setId(id);
            boolean success = modelVersionService.updateModel(modelVersion);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新模型失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有模型（不分页）
     */
    @GetMapping("/all")
    public Result<List<ModelVersion>> getAllModels() {
        List<ModelVersion> models = modelVersionService.getAllModels();
        return Result.success(models);
    }
}
