package com.oda.springboot.controller;

import com.oda.springboot.common.Result;
import com.oda.springboot.entity.PatientVisitRecord;
import com.oda.springboot.entity.User;
import com.oda.springboot.service.IPatientVisitService;
import com.oda.springboot.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-visit")
public class PatientVisitController {

    private static final Logger log = LoggerFactory.getLogger(PatientVisitController.class);

    private final IPatientVisitService patientVisitService;

    public PatientVisitController(IPatientVisitService patientVisitService) {
        this.patientVisitService = patientVisitService;
    }

    @PostMapping
    public Result<PatientVisitRecord> add(@RequestBody PatientVisitRecord record) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            record.setUserId(currentUser.getId());
            PatientVisitRecord saved = patientVisitService.add(record);
            return Result.success("新增成功", saved);
        } catch (Exception e) {
            log.error("新增诊疗记录失败", e);
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<PatientVisitRecord>> getMyRecords(
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            List<PatientVisitRecord> list = patientVisitService.getMyRecords(currentUser.getId(), limit);
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询诊疗记录失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<PatientVisitRecord> getById(@PathVariable Integer id) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            PatientVisitRecord record = patientVisitService.getById(id);
            if (record == null) {
                return Result.error("记录不存在");
            }
            if (!record.getUserId().equals(currentUser.getId())) {
                return Result.error("403", "无权查看该记录");
            }
            return Result.success(record);
        } catch (Exception e) {
            log.error("查询诊疗记录详情失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody PatientVisitRecord record) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            PatientVisitRecord existing = patientVisitService.getById(id);
            if (existing == null) {
                return Result.error("记录不存在");
            }
            if (!existing.getUserId().equals(currentUser.getId())) {
                return Result.error("403", "无权修改该记录");
            }
            record.setId(id);
            patientVisitService.update(record);
            return Result.success("修改成功");
        } catch (Exception e) {
            log.error("修改诊疗记录失败", e);
            return Result.error("修改失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            patientVisitService.delete(id, currentUser.getId());
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除诊疗记录失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/my/chart-data")
    public Result<List<PatientVisitRecord>> getChartData() {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            List<PatientVisitRecord> list = patientVisitService.getChartData(currentUser.getId());
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询图表数据失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/restore/{id}")
    public Result<String> restore(@PathVariable Integer id) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            patientVisitService.restore(id, currentUser.getId());
            return Result.success("恢复成功");
        } catch (Exception e) {
            log.error("恢复诊疗记录失败", e);
            return Result.error("恢复失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch-save")
    public Result<Map<String, Integer>> batchSave(@RequestBody List<PatientVisitRecord> records) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            Map<String, Integer> result = patientVisitService.batchSave(records, currentUser.getId());
            return Result.success("批量保存完成", result);
        } catch (Exception e) {
            log.error("批量保存诊疗记录失败", e);
            return Result.error("批量保存失败: " + e.getMessage());
        }
    }
}
