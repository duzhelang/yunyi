package com.oda.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oda.springboot.entity.PatientVisitRecord;
import com.oda.springboot.mapper.PatientVisitRecordMapper;
import com.oda.springboot.service.IPatientVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientVisitServiceImpl implements IPatientVisitService {

    private static final Logger log = LoggerFactory.getLogger(PatientVisitServiceImpl.class);

    private final PatientVisitRecordMapper mapper;

    @Value("${patient-visit.max-records:500}")
    private int maxRecords;

    public PatientVisitServiceImpl(PatientVisitRecordMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PatientVisitRecord add(PatientVisitRecord record) {
        if (record.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (record.getRecordType() == null || record.getRecordType().isEmpty()) {
            throw new RuntimeException("记录类型不能为空");
        }
        if (!record.getRecordType().matches("visit|self_check|ai_plan")) {
            throw new RuntimeException("记录类型无效，必须为 visit/self_check/ai_plan");
        }
        if (record.getRecordDate() == null) {
            record.setRecordDate(new Date());
        }
        if (record.getIsDeleted() == null) {
            record.setIsDeleted(0);
        }
        validateHealthMetrics(record);
        mapper.insert(record);
        return record;
    }

    @Override
    public PatientVisitRecord getById(Integer id) {
        PatientVisitRecord record = mapper.selectById(id);
        if (record == null || record.getIsDeleted() == 1) {
            return null;
        }
        return record;
    }

    @Override
    public List<PatientVisitRecord> getMyRecords(Integer userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        LambdaQueryWrapper<PatientVisitRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientVisitRecord::getUserId, userId)
                .eq(PatientVisitRecord::getIsDeleted, 0)
                .orderByDesc(PatientVisitRecord::getCreateTime)
                .last("LIMIT " + limit);
        return mapper.selectList(wrapper);
    }

    @Override
    public List<PatientVisitRecord> getChartData(Integer userId) {
        LambdaQueryWrapper<PatientVisitRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientVisitRecord::getUserId, userId)
                .eq(PatientVisitRecord::getIsDeleted, 0)
                .and(w -> w.isNotNull(PatientVisitRecord::getGlucoseFasting)
                        .or().isNotNull(PatientVisitRecord::getGlucosePostprandial)
                        .or().isNotNull(PatientVisitRecord::getBloodPressureSystolic)
                        .or().isNotNull(PatientVisitRecord::getWeight)
                        .or().isNotNull(PatientVisitRecord::getBmi))
                .orderByAsc(PatientVisitRecord::getRecordDate);
        return mapper.selectList(wrapper);
    }

    @Override
    public void update(PatientVisitRecord record) {
        if (record.getId() == null) {
            throw new RuntimeException("记录ID不能为空");
        }
        PatientVisitRecord existing = mapper.selectById(record.getId());
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new RuntimeException("记录不存在");
        }
        validateHealthMetrics(record);
        record.setUserId(existing.getUserId());
        record.setCreateTime(existing.getCreateTime());
        mapper.updateById(record);
    }

    private void validateHealthMetrics(PatientVisitRecord record) {
        if (record.getGlucoseFasting() != null) {
            double val = record.getGlucoseFasting().doubleValue();
            if (val < 1.0 || val > 35.0) {
                throw new RuntimeException("空腹血糖值异常，正常范围1.0-35.0 mmol/L");
            }
        }
        if (record.getGlucosePostprandial() != null) {
            double val = record.getGlucosePostprandial().doubleValue();
            if (val < 1.0 || val > 40.0) {
                throw new RuntimeException("餐后血糖值异常，正常范围1.0-40.0 mmol/L");
            }
        }
        if (record.getHba1c() != null) {
            double val = record.getHba1c().doubleValue();
            if (val < 3.0 || val > 18.0) {
                throw new RuntimeException("糖化血红蛋白值异常，正常范围3.0-18.0%");
            }
        }
        if (record.getBloodPressureSystolic() != null) {
            int val = record.getBloodPressureSystolic();
            if (val < 60 || val > 260) {
                throw new RuntimeException("收缩压值异常，正常范围60-260 mmHg");
            }
        }
        if (record.getBloodPressureDiastolic() != null) {
            int val = record.getBloodPressureDiastolic();
            if (val < 30 || val > 160) {
                throw new RuntimeException("舒张压值异常，正常范围30-160 mmHg");
            }
        }
        if (record.getWeight() != null) {
            double val = record.getWeight().doubleValue();
            if (val < 20.0 || val > 250.0) {
                throw new RuntimeException("体重值异常，正常范围20-250 kg");
            }
        }
        if (record.getBmi() != null) {
            double val = record.getBmi().doubleValue();
            if (val < 10.0 || val > 60.0) {
                throw new RuntimeException("BMI值异常，正常范围10-60");
            }
        }
    }

    @Override
    public void delete(Integer id, Integer userId) {
        PatientVisitRecord existing = mapper.selectById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new RuntimeException("记录不存在或无权操作");
        }
        existing.setIsDeleted(1);
        mapper.updateById(existing);
    }

    @Override
    public void cleanupOldRecords(Integer userId) {
        LambdaQueryWrapper<PatientVisitRecord> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(PatientVisitRecord::getUserId, userId)
                .eq(PatientVisitRecord::getIsDeleted, 0);
        Long count = mapper.selectCount(countWrapper);

        if (count != null && count > maxRecords) {
            long toArchive = count - maxRecords;
            List<PatientVisitRecord> oldest = mapper.selectList(
                    new LambdaQueryWrapper<PatientVisitRecord>()
                            .eq(PatientVisitRecord::getUserId, userId)
                            .eq(PatientVisitRecord::getIsDeleted, 0)
                            .orderByAsc(PatientVisitRecord::getCreateTime)
                            .last("LIMIT " + toArchive)
            );
            for (PatientVisitRecord r : oldest) {
                r.setIsDeleted(1);
                mapper.updateById(r);
            }
            log.info("用户{}归档了{}条过期诊疗记录（软删除）", userId, oldest.size());
        }
    }

    @Override
    public void restore(Integer id, Integer userId) {
        PatientVisitRecord record = mapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该记录");
        }
        record.setIsDeleted(0);
        mapper.updateById(record);
        log.info("用户{}恢复了诊疗记录{}", userId, id);
    }

    @Override
    public Map<String, Integer> batchSave(List<PatientVisitRecord> records, Integer userId) {
        int successCount = 0;
        int failCount = 0;
        for (PatientVisitRecord record : records) {
            try {
                record.setUserId(userId);
                add(record);
                successCount++;
            } catch (Exception e) {
                log.warn("批量保存单条记录失败: {}", e.getMessage());
                failCount++;
            }
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return result;
    }
}
