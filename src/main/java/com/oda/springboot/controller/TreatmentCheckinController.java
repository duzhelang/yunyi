package com.oda.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.TreatmentCheckin;
import com.oda.springboot.entity.User;
import com.oda.springboot.mapper.TreatmentCheckinMapper;
import com.oda.springboot.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/treatment")
public class TreatmentCheckinController {

    private static final Logger log = LoggerFactory.getLogger(TreatmentCheckinController.class);

    @Autowired
    private TreatmentCheckinMapper checkinMapper;

    @PostMapping("/checkin")
    public Result checkin(@RequestBody Map<String, Object> request) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }

            Long profileId = Long.valueOf(request.get("profileId").toString());
            String taskId = (String) request.get("taskId");
            Boolean status = (Boolean) request.get("status");

            QueryWrapper<TreatmentCheckin> wrapper = new QueryWrapper<>();
            wrapper.eq("profile_id", profileId)
                   .eq("task_id", taskId)
                   .eq("checkin_date", LocalDate.now());

            TreatmentCheckin existing = checkinMapper.selectOne(wrapper);

            if (existing != null) {
                existing.setStatus(status ? 1 : 0);
                existing.setUpdatedAt(LocalDateTime.now());
                checkinMapper.updateById(existing);
            } else {
                TreatmentCheckin checkin = new TreatmentCheckin();
                checkin.setProfileId(profileId);
                checkin.setUserId(currentUser.getId().longValue());
                checkin.setTaskId(taskId);
                checkin.setStatus(status ? 1 : 0);
                checkin.setCheckinDate(LocalDate.now());
                checkin.setCreatedAt(LocalDateTime.now());
                checkin.setUpdatedAt(LocalDateTime.now());
                checkinMapper.insert(checkin);
            }

            return Result.success("打卡成功");
        } catch (Exception e) {
            log.error("打卡失败", e);
            return Result.error("打卡失败: " + e.getMessage());
        }
    }

    @GetMapping("/checkin/list")
    public Result getCheckinList(@RequestParam Long profileId, @RequestParam(required = false) String date) {
        try {
            QueryWrapper<TreatmentCheckin> wrapper = new QueryWrapper<>();
            wrapper.eq("profile_id", profileId);
            if (date != null) {
                wrapper.eq("checkin_date", date);
            }
            wrapper.orderByDesc("checkin_date");

            List<TreatmentCheckin> list = checkinMapper.selectList(wrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("records", list);
            result.put("total", list.size());

            return Result.success(result);
        } catch (Exception e) {
            log.error("查询打卡记录失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
