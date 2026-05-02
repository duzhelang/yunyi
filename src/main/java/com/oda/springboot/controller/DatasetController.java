package com.oda.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.Files;
import com.oda.springboot.mapper.FileMapper;
import com.oda.springboot.service.FileScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dataset")
@Slf4j
@CrossOrigin
public class DatasetController {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileScanService fileScanService;

    /**
     * 分页查询训练集列表
     */
    @GetMapping("/list")
    public Result<Page<Files>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        try {
            LambdaQueryWrapper<Files> queryWrapper = new LambdaQueryWrapper<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.like(Files::getName, keyword);
            }

            if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
                queryWrapper.eq(Files::getCategory, category);
            }

            queryWrapper.eq(Files::getIsDelete, false);
            queryWrapper.orderByDesc(Files::getCreateTime);

            Page<Files> page = new Page<>(pageNum, pageSize);
            Page<Files> result = fileMapper.selectPage(page, queryWrapper);

            return Result.success(result);
        } catch (Exception e) {
            log.error("查询训练集列表失败", e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个训练集详情
     */
    @GetMapping("/{id}")
    public Result<Files> getDetail(@PathVariable Integer id) {
        Files file = fileMapper.selectById(id);
        if (file == null) {
            return Result.error("训练集不存在");
        }
        return Result.success(file);
    }

    /**
     * 删除训练集
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        Files file = fileMapper.selectById(id);
        if (file == null) {
            return Result.error("训练集不存在");
        }
        
        file.setIsDelete(true);
        fileMapper.updateById(file);
        
        return Result.success(null);
    }

    /**
     * 扫描数据目录
     */
    @PostMapping("/scan")
    public Result<Map<String, Object>> scanFiles() {
        Map<String, Object> result = fileScanService.scanFiles();
        return Result.success(result);
    }

    /**
     * 获取所有训练集（下拉选择用）
     */
    @GetMapping("/all")
    public Result<java.util.List<Files>> getAll() {
        try {
            LambdaQueryWrapper<Files> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Files::getIsDelete, false);
            queryWrapper.eq(Files::getEnable, true);
            queryWrapper.orderByDesc(Files::getCreateTime);
            return Result.success(fileMapper.selectList(queryWrapper));
        } catch (Exception e) {
            log.error("查询所有训练集失败", e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }
}
