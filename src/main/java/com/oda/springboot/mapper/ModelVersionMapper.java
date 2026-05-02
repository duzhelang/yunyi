package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.ModelVersion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModelVersionMapper extends BaseMapper<ModelVersion> {
}
