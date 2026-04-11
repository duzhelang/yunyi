package com.cucn.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @BelongsProject: Software-CUCN
 * @BelongsPackage: com.cucn.springboot.entity
 * @Author: DZL-125  
 * @CreateTime: 2026-04-01  17:02
 * @Description: 测试文件实体类
 * @Version: 1.0
 */
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getJsonUrl() {
        return jsonUrl;
    }

    public void setJsonUrl(String jsonUrl) {
        this.jsonUrl = jsonUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}