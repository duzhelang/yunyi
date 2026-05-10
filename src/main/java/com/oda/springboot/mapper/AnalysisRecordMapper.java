package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.AnalysisRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分析记录Mapper接口
 */
@Mapper
public interface AnalysisRecordMapper extends BaseMapper<AnalysisRecord> {
}
