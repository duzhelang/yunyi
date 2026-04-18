package com.oda.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oda.springboot.entity.Message;

import com.oda.springboot.entity.User;
import com.oda.springboot.mapper.MessageMapper;
import com.oda.springboot.mapper.UserMapper;
import com.oda.springboot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.oda.springboot.utils.TokenUtils.getCurrentUser;

/**
 * @BelongsProject: Software-ODA
 * @BelongsPackage: com.oda.springboot.service.impl
 * @Author: DZL-125  
 * @CreateTime: 2026-03-29  15:48
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void saveMessage(Message message) {
        User currentUser = getCurrentUser();
        message.setSendUserId(currentUser.getId());
        message.setSendUserName(currentUser.getUsername());
        message.setSendRealName(currentUser.getNickname());
        messageMapper.insert(message);
    }

    @Override
    public void updateMessage(Message message) {
        LambdaQueryWrapper<Message> LambdaqueryWrapper = new LambdaQueryWrapper<>();
        LambdaqueryWrapper.eq(Message::getId,message.getId());
        Message newMessage = messageMapper.selectOne(LambdaqueryWrapper);

        User currentUser = getCurrentUser();
        newMessage.setSendUserId(currentUser.getId());

        newMessage.setSendUserName(currentUser.getUsername());
        newMessage.setSendRealName(currentUser.getNickname());

        newMessage.setTitle(message.getTitle());
        newMessage.setContent(message.getContent());
        newMessage.setType(message.getType());
        newMessage.setReceiveUserName(message.getReceiveUserName());
        messageMapper.updateById(newMessage);
    }
}
