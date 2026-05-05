package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.PatientVisitRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientVisitRecordMapper extends BaseMapper<PatientVisitRecord> {
}
