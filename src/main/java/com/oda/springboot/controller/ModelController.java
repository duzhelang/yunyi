package com.oda.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Constants;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.Files;
import com.oda.springboot.mapper.FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private FileMapper fileMapper;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file,
                         @RequestParam String name,
                         @RequestParam(required = false) String version,
                         @RequestParam(required = false) String remark,
                         @RequestParam(required = false) String description) {
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            return Result.error("400", "文件名不能为空");
        }
        if (file.isEmpty()) {
            return Result.error("400", "上传文件不能为空");
        }

        try {
            String type = FileUtil.extName(originalFilename);
            long size = file.getSize();

            String originalName = FileUtil.mainName(originalFilename);
            String safeName = originalName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            long timestamp = System.currentTimeMillis();
            String fileUUID = safeName + "_" + timestamp + StrUtil.DOT + type;

            File uploadFile = new File(new File(fileUploadPath).getAbsoluteFile(), fileUUID);
            File parentFile = uploadFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            String md5 = SecureUtil.md5(file.getInputStream());
            file.transferTo(uploadFile);

            String url = "http://" + serverIp + ":9090/file/" + fileUUID;

            String userId = stringRedisTemplate.opsForValue().get("userId");
            if (ObjectUtils.isEmpty(userId)) {
                return Result.error("401", "用户未登录");
            }

            Files saveFile = new Files();
            saveFile.setName(StrUtil.isNotBlank(name) ? name : originalFilename);
            saveFile.setType(type);
            saveFile.setSize(size / 1024);
            saveFile.setUrl(url);
            saveFile.setMd5(md5);
            saveFile.setRemark(StrUtil.isNotBlank(remark) ? remark : description);
            saveFile.setCategory("model");
            saveFile.setIsDelete(false);
            saveFile.setEnable(true);
            saveFile.setCreateTime(new Date());
            saveFile.setUserid(Integer.parseInt(userId));
            fileMapper.insert(saveFile);

            flushRedis(Constants.FILES_KEY);
            log.info("模型上传成功: {}, 文件名: {}, 大小: {}KB", name, fileUUID, size / 1024);
            return Result.success("模型上传成功");

        } catch (IOException e) {
            log.error("模型文件上传失败", e);
            return Result.error("500", "文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String remark, @RequestParam(required = false) String name) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        if (remark != null && !remark.isEmpty()) {
            queryWrapper.eq("remark", remark);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        List<Files> list = fileMapper.selectList(queryWrapper);
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(required = false) String remark, @RequestParam(required = false) String name) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        if (remark != null && !remark.isEmpty()) {
            queryWrapper.eq("remark", remark);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        Page<Files> page = fileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        Files file = fileMapper.selectById(id);
        return Result.success(file);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Files file) {
        fileMapper.updateById(file);
        return Result.success("模型更新成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        fileMapper.deleteById(id);
        return Result.success("模型删除成功");
    }

    private void flushRedis(String key) {
        stringRedisTemplate.delete(key);
    }
}
