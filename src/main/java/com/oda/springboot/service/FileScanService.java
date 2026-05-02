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
        Map<String, Object> result = new HashMap<>();
        result.put("newFiles", 0);
        result.put("updatedFiles", 0);
        result.put("totalFiles", 0);

        String projectRoot = System.getProperty("user.dir");
        String[] scanPaths = {
            projectRoot + File.separator + "data",
            projectRoot + File.separator + "python" + File.separator + "data"
        };

        for (String scanPath : scanPaths) {
            File dir = new File(scanPath);
            if (!dir.exists()) {
                dir.mkdirs();
                log.info("创建扫描目录: {}", scanPath);
            }
        }

        for (String scanPath : scanPaths) {
            File baseDir = new File(scanPath);
            if (!baseDir.exists() || !baseDir.isDirectory()) {
                continue;
            }

            File[] files = baseDir.listFiles();
            if (files == null) {
                continue;
            }

            String category = scanPath.contains("python" + File.separator + "data") ? "test" : "train";

            log.info("开始扫描目录: {}, 发现 {} 个文件", scanPath, files.length);

            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    String extension = getFileExtension(fileName);

                    if (FILE_TYPE_MAP.containsKey(extension)) {
                        processFile(file, extension, category, result);
                    }
                }
            }
        }

        log.info("文件扫描完成: 新增 {} 个文件, 更新 {} 个文件",
                result.get("newFiles"), result.get("updatedFiles"));

        return result;
    }

    private void processFile(File file, String extension, String category, Map<String, Object> result) {
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
            log.debug("新增文件记录: {}", fileName);
        } else {
            updateFileRecord(existingFile, file);
            fileMapper.updateById(existingFile);
            result.put("updatedFiles", (Integer) result.get("updatedFiles") + 1);
            log.debug("更新文件记录: {}", fileName);
        }

        result.put("totalFiles", (Integer) result.get("totalFiles") + 1);
    }

    private Files createFileRecord(File file, String relativePath, String extension, String category) {
        Files record = new Files();
        record.setName(file.getName());
        record.setType(extension);
        record.setUrl(relativePath);
        record.setPythonurl(relativePath);
        record.setSize(file.length());
        record.setMd5(calculateFileHash(file));
        record.setIsDelete(false);
        record.setEnable(true);
        record.setCreateTime(new Date());
        record.setUserid(1);
        record.setCategory(category);

        long lineCount = countLines(file);
        String remark = String.format("自动扫描 | 来源: %s | 分类: %s | 行数: %d | 大小: %s",
                category.equals("train") ? "data目录" : "python/data目录",
                category.equals("train") ? "训练集" : "测试集",
                lineCount,
                formatFileSize(file.length()));
        record.setRemark(remark);

        return record;
    }

    private void updateFileRecord(Files record, File file) {
        record.setSize(file.length());
        record.setMd5(calculateFileHash(file));
        long lineCount = countLines(file);
        String category = record.getCategory();
        String remark = String.format("自动扫描 | 来源: %s | 分类: %s | 行数: %d | 大小: %s",
                category.equals("train") ? "data目录" : "python/data目录",
                category.equals("train") ? "训练集" : "测试集",
                lineCount,
                formatFileSize(file.length()));
        record.setRemark(remark);
    }

    private long countLines(File file) {
        try {
            return FileUtil.readLines(file, StandardCharsets.UTF_8).size();
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
            return fullPath.substring(projectRoot.length());
        }
        return fullPath;
    }

    private String calculateFileHash(File file) {
        return String.valueOf(file.lastModified());
    }
}
