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
import java.util.List;

@Service
public class PatientVisitServiceImpl implements IPatientVisitService {

    private static final Logger log = LoggerFactory.getLogger(PatientVisitServiceImpl.class);

    private final PatientVisitRecordMapper mapper;

    @Value("${patient-visit.max-records:30}")
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
        if (record.getRecordDate() == null) {
            record.setRecordDate(new Date());
        }
        if (record.getIsDeleted() == null) {
            record.setIsDeleted(0);
        }
        mapper.insert(record);
        cleanupOldRecords(record.getUserId());
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
        record.setUserId(existing.getUserId());
        record.setCreateTime(existing.getCreateTime());
        mapper.updateById(record);
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
            long toDelete = count - maxRecords;
            List<PatientVisitRecord> oldest = mapper.selectList(
                    new LambdaQueryWrapper<PatientVisitRecord>()
                            .eq(PatientVisitRecord::getUserId, userId)
                            .eq(PatientVisitRecord::getIsDeleted, 0)
                            .orderByAsc(PatientVisitRecord::getCreateTime)
                            .last("LIMIT " + toDelete)
            );
            for (PatientVisitRecord r : oldest) {
                mapper.deleteById(r.getId());
            }
            log.info("用户{}清理了{}条过期诊疗记录", userId, oldest.size());
        }
    }
}
