package com.oda.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oda.springboot.entity.Files;
import com.oda.springboot.mapper.FileMapper;
import com.oda.springboot.common.Constants;
import com.oda.springboot.common.Result;
import com.oda.springboot.utils.PropertyUtil;
import com.oda.springboot.utils.UsePythonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/python")
@Transactional
public class pythonController {

    @Autowired
    private PropertyUtil propertyUtil;

    @Value("${server.ip}")
    private String serverIp;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        String originalName = FileUtil.mainName(originalFilename);
        String safeName = originalName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        long timestamp = System.currentTimeMillis();
        String fileUUID = safeName + "_" + timestamp + StrUtil.DOT + type;

        File uploadFile = new File(propertyUtil.getPythonUpload(), fileUUID);
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String md5 = SecureUtil.md5(file.getInputStream());
        file.transferTo(uploadFile);

        String url = "http://" + serverIp + ":9090/python/" + fileUUID;

        String userId = stringRedisTemplate.opsForValue().get("userId");
        if (ObjectUtils.isEmpty(userId)) {
            return Result.error("401", "用户未登录");
        }

        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        saveFile.setUserid(Integer.parseInt(userId));
        fileMapper.insert(saveFile);

        flushRedis(Constants.FILES_KEY);
        return Result.success("文件上传成功,文件��?" + fileUUID);
    }

    @GetMapping("/getUrl/{url}")
    public Result debugging(@PathVariable String url) throws IOException {
        System.out.println("===== 开始模型训练 =====");
        long stime = System.currentTimeMillis();

        LambdaQueryWrapper<Files> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Files::getUrl, "http://" + serverIp + ":9090/python/" + url);
        Files files = fileMapper.selectOne(queryWrapper);

        if (ObjectUtils.isEmpty(files)) {
            return Result.error("504", "文件不存在");
        }

        // 检查是否已训练 (判断是否已有模型路径记录)
        if (!ObjectUtils.isEmpty(files.getPythonurl()) && files.getPythonurl().endsWith(".pth")) {
            return Result.error("505", "已完成,请下载");
        }

        File csvFile = new File(propertyUtil.getPythonUpload(), url);
        if (!csvFile.exists()) {
            return Result.error("506", "CSV 文件不存在");
        }

        // [核心修改]使用配置文件中的模型路径
        String modelSavePath = propertyUtil.getPythonModelPath();
        if (modelSavePath == null || modelSavePath.isEmpty()) {
            modelSavePath = System.getProperty("user.dir") + "/models/diabetes_model.pth";
        }

        // 确保模型目录存在
        File modelFile = new File(modelSavePath);
        File modelDir = modelFile.getParentFile();
        if (modelDir != null && !modelDir.exists()) {
            modelDir.mkdirs();
        }

        try {
            // (python 和脚本路径)
            // 0: python.exe
            // 1: train.py
            // 2: 输入 CSV
            // 3: 输出模型路径 (.pth)
            int exitCode = UsePythonUtils.callPython(new String[]{
                    propertyUtil.getPythonExe(),
                    propertyUtil.getPythonTrainMain(),
                    csvFile.getAbsolutePath(),
                    modelFile.getAbsolutePath()
            });

            if (exitCode != 0) {
                return Result.error("507", "训练失败,Python 返回值" + exitCode);
            }

            // [核心修改]验证PyTorch 生成的三个文件
            String encoderPath = modelSavePath.replace(".pth", "_encoder.pkl");
            String scalerPath = modelSavePath.replace(".pth", "_scaler.pkl");

            if (!modelFile.exists()) {
                return Result.error("508", "训练完成但未生成主模型文件(.pth)");
            }
            if (!new File(encoderPath).exists()) {
                return Result.error("508", "训练完成但未生成编码器文件(_encoder.pkl)");
            }
            if (!new File(scalerPath).exists()) {
                return Result.error("508", "训练完成但未生成缩放器文件(_scaler.pkl)");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.error("509", "训练被中断" + e.getMessage());
        }

        // 更新数据库,存储模型文件名
        files.setPythonurl(modelFile.getName());
        fileMapper.updateById(files);
        flushRedis(Constants.FILES_KEY);

        long etime = System.currentTimeMillis();
        System.out.printf("训练完成,耗时:%d 毫秒%n", (etime - stime));
        System.out.println("===== 模型训练结束 =====");
        return Result.success("训练成功,模型文件:" + modelFile.getName());
    }

    @GetMapping("/{pythonUrl}")
    public void download(@PathVariable String pythonUrl, HttpServletResponse response) throws IOException {
        // 从配置的下载目录读取
        File downloadFile = new File(propertyUtil.getPythonDownload(), pythonUrl);

        // 如果直接在根目录没找�?尝试在子目录�?视具体保存逻辑而定,这里按配置根目录处理)
        if (!downloadFile.exists()) {
            // 尝试绝对路径查找(�?pythonUrl 存的是全�?
            downloadFile = new File(pythonUrl);
        }

        if (!downloadFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存�?" + pythonUrl);
            return;
        }

        response.addHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(downloadFile.getName(), "UTF-8"));
        response.setContentType("application/octet-stream");

        try (ServletOutputStream os = response.getOutputStream()) {
            os.write(FileUtil.readBytes(downloadFile));
            os.flush();
        }
    }

    @PostMapping("/update")
    public Result update(@RequestBody Files files) {
        fileMapper.updateById(files);
        flushRedis(Constants.FILES_KEY);
        return Result.success();
    }

    private void flushRedis(String key) {
        stringRedisTemplate.delete(key);
    }
}
