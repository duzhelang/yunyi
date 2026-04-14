package com.oda.springboot.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.entity.Files;
import com.oda.springboot.mapper.FileMapper;
import com.oda.springboot.common.Constants;
import com.oda.springboot.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMethod;
@RestController
@RequestMapping("/file")
public class FileController {

    // 总目录
    @Value("${files.upload.path}")
    private String fileUploadPath;

    // 头像目录
    @Value("${files.avatar.path}")
    private String avatarUploadPath;

    // 普通文件目录
    @Value("${files.common.path}")
    private String commonUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Resource
    private FileMapper fileMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // ======================== 1. 上传头像(专用,存在 avatar 文件夹)========================
    @PostMapping("/upload/avatar")
    public String uploadAvatar(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;

        // 头像专用路径
        File uploadFile = new File(new File(avatarUploadPath).getAbsoluteFile(), uniqueFileName);
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        file.transferTo(uploadFile);
        String url = "http://" + serverIp + ":9090/file/avatar/" + uniqueFileName;
        flushRedis(Constants.FILES_KEY);
        return url;
    }

    // ======================== 2. 上传普通文件(存在 common 文件夹)========================
    @RequestMapping(value = "/upload/common", method = {RequestMethod.GET, RequestMethod.POST})
    public String uploadCommon(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;

//        File uploadFile = new File(commonUploadPath, uniqueFileName);
        File uploadFile = new File(new File(commonUploadPath).getAbsoluteFile(), uniqueFileName);
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        file.transferTo(uploadFile);
        String url = "http://" + serverIp + ":9090/file/common/" + uniqueFileName;
        flushRedis(Constants.FILES_KEY);
        return url;
    }

    // ======================== 3. 通用上传(保留兼容)========================
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;

//        File uploadFile = new File(fileUploadPath, uniqueFileName);
        File uploadFile = new File(new File(fileUploadPath).getAbsoluteFile(), uniqueFileName);
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        file.transferTo(uploadFile);
        String url = "http://" + serverIp + ":9090/file/" + uniqueFileName;
        flushRedis(Constants.FILES_KEY);
        return url;
    }

    // ======================== 下载接口(支持子文件夹)========================
    @GetMapping("/{type}/{fileUUID}")
    public void download(@PathVariable String type,
                         @PathVariable String fileUUID,
                         HttpServletResponse response) throws IOException {
        String basePath;
        switch (type) {
            case "avatar": basePath = avatarUploadPath; break;
            case "common": basePath = commonUploadPath; break;
            default: basePath = fileUploadPath;
        }

        File uploadFile = new File(basePath + fileUUID);
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }
    private Files getFileByMd5(String md5) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Files files) {
        fileMapper.updateById(files);
        flushRedis(Constants.FILES_KEY);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(fileMapper.selectById(id));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        fileMapper.deleteById(id);
        flushRedis(Constants.FILES_KEY);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Files> files = fileMapper.selectList(queryWrapper);
        for (Files file : files) {
            fileMapper.deleteById(file);
        }
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like(name, name);
        }
        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }

    private void flushRedis(String key) {
        stringRedisTemplate.delete(key);
    }
}
