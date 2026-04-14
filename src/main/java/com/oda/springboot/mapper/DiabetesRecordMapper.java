package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.DiabetesRecord;

public interface DiabetesRecordMapper extends BaseMapper<DiabetesRecord> {
    // 继承BaseMapper,自动获得CRUD方法
}
