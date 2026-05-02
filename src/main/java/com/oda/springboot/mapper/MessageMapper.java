package com.oda.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oda.springboot.entity.Message;

/**
 * @BelongsProject: Software-ODA
 * @BelongsPackage: com.oda.springboot.mapper
 * @Author: ODA-cj  
 * @CreateTime: 2026-03-29  18:07
 * @Description: TODO
 * @Version: 1.0
 */
public interface MessageMapper extends BaseMapper<Message> {
    int insertSelective(Message record);
}
