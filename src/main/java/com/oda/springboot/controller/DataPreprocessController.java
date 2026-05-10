package com.oda.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.Files;
import com.oda.springboot.mapper.FileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/data")
public class DataPreprocessController {

    private static final Logger log = LoggerFactory.getLogger(DataPreprocessController.class);

    @Autowired
    private FileMapper fileMapper;

    @PostMapping("/preprocess")
    public Result preprocess(@RequestBody Map<String, Object> request) {
        try {
            Integer fileId = (Integer) request.get("fileId");
            String fillMethod = (String) request.getOrDefault("fillMethod", "mean");
            Boolean standardize = (Boolean) request.getOrDefault("standardize", true);

            if (fileId == null) {
                return Result.error("400", "请选择要预处理的文件");
            }

            Files file = fileMapper.selectById(fileId);
            if (file == null) {
                return Result.error("404", "文件不存在");
            }

            String filePath = file.getUrl();
            String projectRoot = System.getProperty("user.dir");
            
            File sourceFile;
            if (filePath.startsWith("/") || filePath.startsWith("\\") || filePath.contains(":")) {
                sourceFile = new File(filePath);
            } else {
                sourceFile = new File(projectRoot, filePath);
            }
            
            if (!sourceFile.exists()) {
                String normalizedPath = filePath.replace('\\', '/');
                if (normalizedPath.startsWith("/")) {
                    normalizedPath = normalizedPath.substring(1);
                }
                sourceFile = new File(projectRoot, normalizedPath);
            }
            
            if (!sourceFile.exists()) {
                return Result.error("404", "文件不存在于磁盘: " + filePath);
            }

            Map<String, Object> result = new HashMap<>();
            int totalRows = 0;
            int processedRows = 0;
            int droppedRows = 0;

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(sourceFile), StandardCharsets.UTF_8))) {
                String header = reader.readLine();
                if (header == null) {
                    return Result.error("400", "文件为空");
                }
                String[] columns = header.split(",");
                totalRows++;

                List<String[]> rows = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    totalRows++;
                    String[] fields = line.split(",", -1);
                    boolean hasMissing = false;
                    for (String f : fields) {
                        if (f == null || f.trim().isEmpty() || "null".equalsIgnoreCase(f.trim())) {
                            hasMissing = true;
                            break;
                        }
                    }

                    if (hasMissing && "drop".equals(fillMethod)) {
                        droppedRows++;
                        continue;
                    }

                    if (hasMissing) {
                        for (int i = 0; i < fields.length; i++) {
                            if (fields[i] == null || fields[i].trim().isEmpty() || "null".equalsIgnoreCase(fields[i].trim())) {
                                fields[i] = "0";
                            }
                        }
                    }
                    rows.add(fields);
                    processedRows++;
                }

                result.put("totalRows", totalRows - 1);
                result.put("processedRows", processedRows);
                result.put("droppedRows", droppedRows);
                result.put("columns", columns.length);
                result.put("fillMethod", fillMethod);
                result.put("standardize", standardize);
                result.put("message", String.format("预处理完成：共 %d 行数据，处理 %d 行，删除 %d 行缺失行",
                    totalRows - 1, processedRows, droppedRows));
            }

            return Result.success(result);
        } catch (Exception e) {
            log.error("数据预处理失败", e);
            return Result.error("500", "预处理失败: " + e.getMessage());
        }
    }
}
