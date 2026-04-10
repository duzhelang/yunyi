package com.cucn.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: Software-CUCN
 * @BelongsPackage: com.cucn.springboot.entity
 * @Author: DZL-125  
 * @CreateTime: 2026-03-29  01:01
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("sys_message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String type;

    private String content;

    @ApiModelProperty("创建时间")
    private Date createTime;

    private Integer sendUserId;

    @TableField("send_user_name")
    private String sendUserName;

    private String sendRealName;

    @TableField("receive_user_name")
    private String receiveUserName;

}
