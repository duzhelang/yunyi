package com.oda.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Constants;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.OnlineDate;
import com.oda.springboot.mapper.ResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: Software-ODA
 * @BelongsPackage: com.oda.springboot.controller
 * @Author: DZL-125  
 * @CreateTime: 2026-03-28  17:12
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/detailbord")
public class DetailbordController {
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Resource
    private ResultMapper resultMapper;
    //private  FileMapper fileMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(resultMapper.selectById(id));
    }

//    @CacheEvict(value="files",key="'frontAll'")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        resultMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/totle")
    public Result totle() {
        List<OnlineDate> onlinedates = resultMapper.selectList(new QueryWrapper<OnlineDate>());
        String today = DateUtil.today();
        Integer totle=0;
        for (OnlineDate onlineDate : onlinedates) {
            Date createTime = onlineDate.getCreateTime();
            String format = DateUtil.format(createTime, "yyyy-MM-dd");
            if (format.equals(today)){
                totle++;
            }

        }
        return Result.success(totle);
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        // select * from sys_file where id in (id,id,id...)
        QueryWrapper<OnlineDate> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<OnlineDate> onlinedates = resultMapper.selectList(queryWrapper);
        for (OnlineDate onlineDate : onlinedates) {
            resultMapper.deleteById(onlineDate);
        }
        return Result.success();
    }

    /**
     * 分页查询接口
     * @return
     */

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String result,
                           @RequestParam(required = false) Integer id) {
        QueryWrapper<OnlineDate> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper.eq("testfile_id", id);
        }
        if (!result.isEmpty()) {
            queryWrapper.like("result", result);
        }
        return Result.success(resultMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }
}

