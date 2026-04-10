package com.cucn.springboot.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cucn.springboot.entity.DiabetesRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiabetesRecordMapper extends BaseMapper<DiabetesRecord> {
    // 继承BaseMapper,自动获得CRUD方法(无需手动写SQL?
}