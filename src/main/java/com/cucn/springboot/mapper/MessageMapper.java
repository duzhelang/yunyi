package com.cucn.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cucn.springboot.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @BelongsProject: Software-CUCN
 * @BelongsPackage: com.cucn.springboot.mapper
 * @Author: DZL-125  
 * @CreateTime: 2026-03-29  18:07
 * @Description: TODO
 * @Version: 1.0
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    int insertSelective(Message record);
}
