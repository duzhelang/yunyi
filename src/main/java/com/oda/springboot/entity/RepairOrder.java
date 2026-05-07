package com.oda.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.util.Date;
import java.util.List;

@TableName(value = "sys_repair_order", autoResultMap = true)
public class RepairOrder {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String orderNo;

    private String title;

    private String faultType;

    private String faultSubType;

    private String urgency;

    private String description;

    private String reproSteps;

    private String pagePath;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> attachments;

    private String contactPhone;

    private String contactEmail;

    private Boolean acceptRemote;

    private String status;

    private Integer submitUserId;

    private String submitUserName;

    private String submitRealName;

    private Integer assignUserId;

    private String assignUserName;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public String getFaultSubType() {
        return faultSubType;
    }

    public void setFaultSubType(String faultSubType) {
        this.faultSubType = faultSubType;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReproSteps() {
        return reproSteps;
    }

    public void setReproSteps(String reproSteps) {
        this.reproSteps = reproSteps;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Boolean getAcceptRemote() {
        return acceptRemote;
    }

    public void setAcceptRemote(Boolean acceptRemote) {
        this.acceptRemote = acceptRemote;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSubmitUserId() {
        return submitUserId;
    }

    public void setSubmitUserId(Integer submitUserId) {
        this.submitUserId = submitUserId;
    }

    public String getSubmitUserName() {
        return submitUserName;
    }

    public void setSubmitUserName(String submitUserName) {
        this.submitUserName = submitUserName;
    }

    public String getSubmitRealName() {
        return submitRealName;
    }

    public void setSubmitRealName(String submitRealName) {
        this.submitRealName = submitRealName;
    }

    public Integer getAssignUserId() {
        return assignUserId;
    }

    public void setAssignUserId(Integer assignUserId) {
        this.assignUserId = assignUserId;
    }

    public String getAssignUserName() {
        return assignUserName;
    }

    public void setAssignUserName(String assignUserName) {
        this.assignUserName = assignUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
