package com.oda.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.OnlineDate;
import com.oda.springboot.mapper.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/detailbord")
public class DetailbordController {

    private static final Logger log = LoggerFactory.getLogger(DetailbordController.class);

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Resource
    private ResultMapper resultMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        try {
            return Result.success(resultMapper.selectById(id));
        } catch (Exception e) {
            log.error("查询预测结果详情失败, id={}", id, e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            resultMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除预测结果失败, id={}", id, e);
            return Result.error("500", "删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/totle")
    public Result totle() {
        try {
            List<OnlineDate> onlinedates = resultMapper.selectList(new QueryWrapper<OnlineDate>());
            String today = DateUtil.today();
            Integer totle = 0;
            for (OnlineDate onlineDate : onlinedates) {
                Date createTime = onlineDate.getCreateTime();
                String format = DateUtil.format(createTime, "yyyy-MM-dd");
                if (format.equals(today)) {
                    totle++;
                }
            }
            return Result.success(totle);
        } catch (Exception e) {
            log.error("查询今日预测总数失败", e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的预测结果");
            }
            QueryWrapper<OnlineDate> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            List<OnlineDate> onlinedates = resultMapper.selectList(queryWrapper);
            for (OnlineDate onlineDate : onlinedates) {
                resultMapper.deleteById(onlineDate);
            }
            return Result.success();
        } catch (Exception e) {
            log.error("批量删除预测结果失败, ids={}", ids, e);
            return Result.error("500", "批量删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String result,
                           @RequestParam(required = false) Integer id) {
        try {
            QueryWrapper<OnlineDate> queryWrapper = new QueryWrapper<>();
            if (id != null) {
                queryWrapper.eq("testfile_id", id);
            }
            if (!result.isEmpty()) {
                queryWrapper.like("result", result);
            }
            queryWrapper.orderByDesc("create_time");
            return Result.success(resultMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
        } catch (Exception e) {
            log.error("分页查询预测结果失败", e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }
}

