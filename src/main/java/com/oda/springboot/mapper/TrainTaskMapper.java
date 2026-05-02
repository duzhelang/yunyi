package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.TrainTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainTaskMapper extends BaseMapper<TrainTask> {
}
