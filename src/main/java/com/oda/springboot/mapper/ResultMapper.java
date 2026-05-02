package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.OnlineDate;
import org.apache.ibatis.annotations.Mapper;


/**
 * @BelongsProject: Software-ODA
 * @BelongsPackage: com.oda.springboot.mapper
 * @Author: ODA-cj  
 * @CreateTime: 2026-03-28  11:32
 * @Description: TODO
 * @Version: 1.0
 */
@Mapper
public interface ResultMapper extends BaseMapper<OnlineDate> {
}
