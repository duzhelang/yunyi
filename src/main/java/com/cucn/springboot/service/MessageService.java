package com.cucn.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cucn.springboot.entity.Message;

import java.util.List;

/**
 * @BelongsProject: Software-CUCN
 * @BelongsPackage: com.cucn.springboot.service
 * @Author: DZL-125  
 * @CreateTime: 2026-03-29  15:45
 * @Description: TODO
 * @Version: 1.0
 */
public interface MessageService extends IService<Message> {

    void  saveMessage(Message message);
    void updateMessage(Message message);
}
