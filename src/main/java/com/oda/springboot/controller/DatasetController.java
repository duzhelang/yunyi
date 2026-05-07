package com.oda.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.Files;
import com.oda.springboot.mapper.FileMapper;
import com.oda.springboot.service.FileScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/dataset")
@Slf4j
@CrossOrigin
public class DatasetController {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileScanService fileScanService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 分页查询数据集列表
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
            log.error("查询数据集列表失败", e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 扫描数据目录（默认扫描训练集目录 data/train）
     */
    @PostMapping("/scan")
    public Result<Map<String, Object>> scanFiles() {
        Map<String, Object> result = fileScanService.scanFiles();
        return Result.success(result);
    }

    /**
     * 扫描测试集目录 data/test
     */
    @PostMapping("/scan-test")
    public Result<Map<String, Object>> scanTestFiles() {
        Map<String, Object> result = fileScanService.scanFiles("data/test");
        return Result.success(result);
    }

    /**
     * 获取数据集统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            LambdaQueryWrapper<Files> trainWrapper = new LambdaQueryWrapper<>();
            trainWrapper.eq(Files::getCategory, "train");
            trainWrapper.eq(Files::getIsDelete, false);
            long trainCount = fileMapper.selectCount(trainWrapper);

            LambdaQueryWrapper<Files> testWrapper = new LambdaQueryWrapper<>();
            testWrapper.eq(Files::getCategory, "test");
            testWrapper.eq(Files::getIsDelete, false);
            long testCount = fileMapper.selectCount(testWrapper);

            stats.put("trainCount", trainCount);
            stats.put("testCount", testCount);
            stats.put("dataFileCount", trainCount + testCount);

            String projectRoot = System.getProperty("user.dir");
            String jsonDir = projectRoot + File.separator + "data" + File.separator + "json";
            File dir = new File(jsonDir);
            long jsonCount = 0;
            if (dir.exists() && dir.isDirectory()) {
                File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
                jsonCount = (jsonFiles != null) ? jsonFiles.length : 0;
            }
            stats.put("jsonCount", jsonCount);

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("500", "获取统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有数据集（下拉选择用）
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
            log.error("查询所有数据集失败", e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取JSON预测结果文件列表
     */
    @GetMapping("/json-files")
    public Result<Map<String, Object>> getJsonFiles(
            @RequestParam(required = false) String keyword) {
        try {
            String projectRoot = System.getProperty("user.dir");
            String jsonDir = projectRoot + File.separator + "data" + File.separator + "json";
            File dir = new File(jsonDir);
            if (!dir.exists()) {
                dir.mkdirs();
                Map<String, Object> empty = new HashMap<>();
                empty.put("files", Collections.emptyList());
                empty.put("total", 0);
                return Result.success(empty);
            }

            File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (jsonFiles == null) {
                Map<String, Object> empty = new HashMap<>();
                empty.put("files", Collections.emptyList());
                empty.put("total", 0);
                return Result.success(empty);
            }

            List<Map<String, Object>> fileList = new ArrayList<>();
            for (File f : jsonFiles) {
                if (StrUtil.isNotBlank(keyword) && !f.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    continue;
                }
                Map<String, Object> info = new HashMap<>();
                info.put("fileName", f.getName());
                info.put("size", f.length());
                info.put("fileSize", formatFileSize(f.length()));
                info.put("lastModified", f.lastModified());
                info.put("path", f.getAbsolutePath());
                fileList.add(info);
            }

            fileList.sort((a, b) -> Long.compare((long) b.get("lastModified"), (long) a.get("lastModified")));

            Map<String, Object> result = new HashMap<>();
            result.put("files", fileList);
            result.put("total", fileList.size());
            result.put("directory", jsonDir);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取JSON结果文件列表失败", e);
            return Result.error("500", "获取结果列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取JSON文件内容预览
     */
    @GetMapping("/json-files/{fileName}")
    public Result<Map<String, Object>> getJsonFileContent(@PathVariable String fileName) {
        try {
            String projectRoot = System.getProperty("user.dir");
            String jsonDir = projectRoot + File.separator + "data" + File.separator + "json";
            File jsonFile = new File(jsonDir, fileName);

            if (!jsonFile.exists() || !jsonFile.getName().endsWith(".json")) {
                return Result.error("404", "结果文件不存在: " + fileName);
            }

            String content = FileUtil.readUtf8String(jsonFile);
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("content", content);
            result.put("size", jsonFile.length());
            result.put("fileSize", formatFileSize(jsonFile.length()));
            result.put("lastModified", jsonFile.lastModified());
            return Result.success(result);
        } catch (Exception e) {
            log.error("读取JSON文件内容失败", e);
            return Result.error("500", "读取失败: " + e.getMessage());
        }
    }

    /**
     * 删除JSON结果文件
     */
    @DeleteMapping("/json-files/{fileName}")
    public Result<Void> deleteJsonFile(@PathVariable String fileName) {
        try {
            String projectRoot = System.getProperty("user.dir");
            String jsonDir = projectRoot + File.separator + "data" + File.separator + "json";
            File jsonFile = new File(jsonDir, fileName);

            if (!jsonFile.exists() || !jsonFile.getName().endsWith(".json")) {
                return Result.error("404", "结果文件不存在: " + fileName);
            }

            boolean deleted = jsonFile.delete();
            if (deleted) {
                log.info("JSON结果文件已删除: {}", fileName);
                return Result.success(null);
            } else {
                return Result.error("500", "删除失败");
            }
        } catch (Exception e) {
            log.error("删除JSON文件失败", e);
            return Result.error("500", "删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个数据集详情（仅匹配数字ID）
     */
    @GetMapping("/{id:\\d+}")
    public Result<Files> getDetail(@PathVariable Integer id) {
        Files file = fileMapper.selectById(id);
        if (file == null) {
            return Result.error("数据集不存在");
        }
        return Result.success(file);
    }

    /**
     * 删除数据集（仅匹配数字ID）
     */
    @DeleteMapping("/{id:\\d+}")
    public Result<Void> delete(@PathVariable Integer id) {
        Files file = fileMapper.selectById(id);
        if (file == null) {
            return Result.error("数据集不存在");
        }
        
        file.setIsDelete(true);
        fileMapper.updateById(file);
        
        return Result.success(null);
    }

    /**
     * 批量删除数据集
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的数据集");
            }
            for (Integer id : ids) {
                Files file = fileMapper.selectById(id);
                if (file != null) {
                    file.setIsDelete(true);
                    fileMapper.updateById(file);
                }
            }
            return Result.success(null);
        } catch (Exception e) {
            log.error("批量删除数据集失败", e);
            return Result.error("500", "批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 上传数据集文件
     */
    @PostMapping("/upload")
    public Result<Files> upload(@RequestParam MultipartFile file,
                                @RequestParam(defaultValue = "train") String category) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (StrUtil.isBlank(originalFilename)) {
                return Result.error("文件名不能为空");
            }

            String ext = FileUtil.extName(originalFilename).toLowerCase();
            if (!Arrays.asList("csv", "xlsx", "xls").contains(ext)) {
                return Result.error("仅支持 CSV、XLSX、XLS 格式");
            }

            long maxSize = 50 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过 50MB");
            }

            String targetDir = "test".equals(category) ? "data/test" : "data/train";
            String projectRoot = System.getProperty("user.dir");
            String fullPath = projectRoot + File.separator + targetDir;
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String safeName = originalFilename.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");
            File destFile = new File(fullPath, safeName);
            file.transferTo(destFile);

            String relativePath = File.separator + targetDir + File.separator + safeName;

            Files record = new Files();
            record.setName(originalFilename);
            record.setType(ext);
            record.setUrl(relativePath);
            record.setPythonurl(relativePath);
            record.setSize(file.getSize());
            record.setMd5(String.valueOf(destFile.lastModified()));
            record.setIsDelete(false);
            record.setEnable(true);
            record.setCreateTime(new Date());
            record.setCategory(category);
            record.setQualityLevel("raw");
            record.setFileSize(formatFileSize(file.getSize()));

            String userId = stringRedisTemplate.opsForValue().get("userId");
            record.setUserid(ObjectUtils.isEmpty(userId) ? 1 : Integer.parseInt(userId));

            if ("csv".equals(ext)) {
                List<String> lines = FileUtil.readLines(destFile, StandardCharsets.UTF_8);
                if (!lines.isEmpty()) {
                    int sampleCount = Math.max(0, lines.size() - 1);
                    record.setSampleCount(sampleCount);

                    String header = lines.get(0);
                    String[] columns = header.split(",");
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < columns.length; i++) {
                        if (i > 0) sb.append(",");
                        sb.append("{\"name\":\"").append(columns[i].trim()).append("\",\"index\":").append(i).append("}");
                    }
                    sb.append("]");
                    record.setColumnInfo(sb.toString());
                }
            }

            String remark = String.format("手动上传 | 分类: %s | 大小: %s",
                    "train".equals(category) ? "训练集" : "测试集",
                    record.getFileSize());
            record.setRemark(remark);

            fileMapper.insert(record);
            log.info("数据集上传成功: {}, 分类: {}", originalFilename, category);

            return Result.success(record);
        } catch (Exception e) {
            log.error("上传数据集失败", e);
            return Result.error("500", "上传失败: " + e.getMessage());
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int unit = 0;
        double dSize = size;
        while (dSize >= 1024 && unit < 4) {
            dSize /= 1024;
            unit++;
        }
        String[] units = {"B", "KB", "MB", "GB"};
        return String.format("%.2f %s", dSize, units[unit]);
    }
}
