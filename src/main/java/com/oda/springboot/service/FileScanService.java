package com.oda.springboot.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oda.springboot.entity.Files;
import com.oda.springboot.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileScanService {

    private static final Log log = Log.get();

    @Autowired
    private FileMapper fileMapper;

    @Value("${base.path:./}")
    private String basePath;

    private static final Map<String, String> FILE_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        FILE_TYPE_MAP.put(".csv", "csv");
        FILE_TYPE_MAP.put(".xlsx", "xlsx");
        FILE_TYPE_MAP.put(".xls", "xls");
    }

    public Map<String, Object> scanFiles() {
        return scanFiles("data/train");
    }

    /**
     * 扫描指定目录的文件
     * @param targetPath 相对于项目根目录的路径，例如 "data/train"
     * @return
     */
    public Map<String, Object> scanFiles(String targetPath) {
        Map<String, Object> result = new HashMap<>();
        result.put("newFiles", 0);
        result.put("updatedFiles", 0);
        result.put("totalFiles", 0);

        String projectRoot = System.getProperty("user.dir");
        String fullPath = projectRoot + File.separator + targetPath;

        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
            log.info("创建扫描目录: {}", fullPath);
        }

        File baseDir = new File(fullPath);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            log.warn("扫描目录不存在: {}", fullPath);
            return result;
        }

        File[] files = baseDir.listFiles();
        if (files == null) {
            return result;
        }

        String category;
        if (targetPath.contains("test")) {
            category = "test";
        } else {
            category = "train";
        }

        log.info("开始扫描目录: {}, 发现 {} 个文件", fullPath, files.length);

        for (File file : files) {
            try {
                if (file.isFile()) {
                    String fileName = file.getName();
                    
                    // 跳过 .gitkeep 等隐藏文件
                    if (fileName.startsWith(".")) {
                        continue;
                    }
                    
                    String extension = getFileExtension(fileName);
                    
                    if (FILE_TYPE_MAP.containsKey(extension)) {
                        processFile(file, extension, category, result);
                    }
                }
            } catch (Exception e) {
                log.error("处理文件时出错: {}", file.getName(), e);
            }
        }

        log.info("数据集扫描完成：新增 {}，更新 {}，总数 {}", result.get("newFiles"), result.get("updatedFiles"), result.get("totalFiles"));

        return result;
    }

    private void processFile(File file, String extension, String category, Map<String, Object> result) {
        try {
            String fileName = file.getName();
            String relativePath = getRelativePath(file);

            LambdaQueryWrapper<Files> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Files::getName, fileName);
            queryWrapper.eq(Files::getUrl, relativePath);
            Files existingFile = fileMapper.selectOne(queryWrapper);

            if (existingFile == null) {
                Files newFile = createFileRecord(file, relativePath, extension, category);
                fileMapper.insert(newFile);
                result.put("newFiles", (Integer) result.get("newFiles") + 1);
                log.info("新增文件记录: {}", fileName);
            } else {
                updateFileRecord(existingFile, file);
                fileMapper.updateById(existingFile);
                result.put("updatedFiles", (Integer) result.get("updatedFiles") + 1);
                log.info("更新文件记录: {}", fileName);
            }

            result.put("totalFiles", (Integer) result.get("totalFiles") + 1);
        } catch (Exception e) {
            log.error("处理文件记录时出错: {}", file.getName(), e);
        }
    }

    private Files createFileRecord(File file, String relativePath, String extension, String category) {
        Files record = new Files();
        record.setName(file.getName());
        record.setType(FILE_TYPE_MAP.get(extension)); // 使用不带点的扩展名
        record.setUrl(relativePath);
        record.setPythonurl(relativePath);
        record.setSize(file.length());
        record.setMd5(calculateFileHash(file));
        record.setIsDelete(false);
        record.setEnable(true);
        record.setCreateTime(new Date());
        record.setUserid(1);
        record.setCategory(category);
        record.setQualityLevel("raw");
        record.setSampleCount((int) countLines(file));
        record.setFileSize(formatFileSize(file.length()));
        record.setColumnInfo(buildColumnInfo(file, extension));

        int lineCount = record.getSampleCount();
        String remark = String.format("自动扫描 | 来源: %s | 分类: %s | 行数: %d | 大小: %s",
                category.equals("train") ? "data目录" : "python/data目录",
                category.equals("train") ? "训练集" : "测试集",
                lineCount,
                record.getFileSize());
        record.setRemark(remark);

        return record;
    }

    private void updateFileRecord(Files record, File file) {
        record.setSize(file.length());
        record.setMd5(calculateFileHash(file));
        record.setSampleCount((int) countLines(file));
        record.setFileSize(formatFileSize(file.length()));
        record.setColumnInfo(buildColumnInfo(file, "." + record.getType()));
        int lineCount = record.getSampleCount();
        String category = record.getCategory();
        String remark = String.format("自动扫描 | 来源: %s | 分类: %s | 行数: %d | 大小: %s",
                category.equals("train") ? "data目录" : "python/data目录",
                category.equals("train") ? "训练集" : "测试集",
                lineCount,
                record.getFileSize());
        record.setRemark(remark);
    }

    private String buildColumnInfo(File file, String extension) {
        try {
            if (!".csv".equals(extension)) {
                return null;
            }
            List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return null;
            }
            String header = lines.get(0);
            String[] columns = header.split(",");
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < columns.length; i++) {
                String col = columns[i].trim();
                if (i > 0) sb.append(",");
                sb.append("{\"name\":\"").append(col).append("\",\"index\":").append(i).append("}");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            log.warn("构建列信息时出错: {}", file.getName(), e);
            return null;
        }
    }

    private long countLines(File file) {
        try {
            List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
            int count = 0;
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    count++;
                }
            }
            // CSV文件减去表头行，只返回数据条数
            if (file.getName().toLowerCase().endsWith(".csv") && count > 0) {
                count = count - 1;
            }
            return count;
        } catch (Exception e) {
            return 0;
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

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex).toLowerCase();
        }
        return "";
    }

    private String getRelativePath(File file) {
        String projectRoot = System.getProperty("user.dir");
        String fullPath = file.getAbsolutePath();
        if (fullPath.startsWith(projectRoot)) {
            String relative = fullPath.substring(projectRoot.length());
            return relative.replace('\\', '/');
        }
        return fullPath.replace('\\', '/');
    }

    private String calculateFileHash(File file) {
        return String.valueOf(file.lastModified());
    }
}
