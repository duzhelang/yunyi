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
 * @CreateTime: 2026-04-01  17:02
 * @Description: 测试文件实体类
 * @Version: 1.0
 */
@Data
@TableName("sys_testfile")
public class TestFiles {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String type;

    private Long size;

    private String url;

    // [重要修改]enable 通常?0 ?1 的整数,不要?String,否则逻辑判断会出?
    private Integer enable;

    private String md5;

    // is_delete 对应数据库的 0/1,Boolean 通常可以自动映射,保持现状即?
    private Boolean isDelete;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @TableField("user_id")
    private int userid;

    // [核心修复]确保数据库列名确实?"jsonUrl" (驼峰)?
    // 如果数据库列名是 "json_url" (下划?,请改为 @TableField("json_url")
    @TableField("jsonUrl")
    private String jsonUrl;

    // 映射数据库的 file_name 字段
    @TableField("file_name")
    private String fileName;
}