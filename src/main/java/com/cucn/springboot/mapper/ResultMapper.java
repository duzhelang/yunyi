package com.cucn.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cucn.springboot.entity.Files;
import com.cucn.springboot.entity.OnlineDate;
import org.apache.ibatis.annotations.Mapper;


/**
 * @BelongsProject: Software-CUCN
 * @BelongsPackage: com.cucn.springboot.mapper
 * @Author: DZL-125  
 * @CreateTime: 2026-03-28  11:32
 * @Description: TODO
 * @Version: 1.0
 */
@Mapper
public interface ResultMapper extends BaseMapper<OnlineDate> {
}
