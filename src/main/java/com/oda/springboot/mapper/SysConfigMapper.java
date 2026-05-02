package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}