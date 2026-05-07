package com.oda.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oda.springboot.entity.RepairEvaluation;
import com.oda.springboot.entity.RepairLog;
import com.oda.springboot.entity.RepairOrder;
import com.oda.springboot.entity.User;
import com.oda.springboot.exception.ServiceException;
import com.oda.springboot.mapper.RepairEvaluationMapper;
import com.oda.springboot.mapper.RepairLogMapper;
import com.oda.springboot.mapper.RepairOrderMapper;
import com.oda.springboot.service.IRepairService;
import com.oda.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements IRepairService {

    private static final AtomicInteger ORDER_SEQ = new AtomicInteger(1000);

    @Resource
    private RepairOrderMapper repairOrderMapper;

    @Resource
    private RepairLogMapper repairLogMapper;

    @Resource
    private RepairEvaluationMapper repairEvaluationMapper;

    @Override
    @Transactional
    public RepairOrder submitOrder(RepairOrder order) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("401", "未登录，请先登录");
        }

        order.setOrderNo(generateOrderNo());
        order.setSubmitUserId(currentUser.getId());
        order.setSubmitUserName(currentUser.getUsername());
        order.setSubmitRealName(currentUser.getNickname());
        order.setStatus("待处理");
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        repairOrderMapper.insert(order);

        RepairLog log = new RepairLog();
        log.setOrderId(order.getId());
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setAction("提交");
        log.setContent("提交报修工单：" + order.getTitle());
        log.setIsVisibleToUser(true);
        log.setCreateTime(new Date());
        repairLogMapper.insert(log);

        return order;
    }

    @Override
    public Page<RepairOrder> getMyOrders(Integer pageNum, Integer pageSize, String status, String keyword) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("401", "未登录，请先登录");
        }

        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getSubmitUserId, currentUser.getId());
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(RepairOrder::getTitle, keyword)
                    .or().like(RepairOrder::getOrderNo, keyword));
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return repairOrderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Page<RepairOrder> getAllOrders(Integer pageNum, Integer pageSize, String status, String urgency, String keyword) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        if (StrUtil.isNotBlank(urgency)) {
            wrapper.eq(RepairOrder::getUrgency, urgency);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(RepairOrder::getTitle, keyword)
                    .or().like(RepairOrder::getOrderNo, keyword)
                    .or().like(RepairOrder::getSubmitRealName, keyword));
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return repairOrderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public RepairOrder getOrderDetail(Integer id) {
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("500", "工单不存在");
        }
        return order;
    }

    @Override
    public List<RepairLog> getOrderLogs(Integer orderId, boolean onlyVisible) {
        LambdaQueryWrapper<RepairLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairLog::getOrderId, orderId);
        if (onlyVisible) {
            wrapper.eq(RepairLog::getIsVisibleToUser, true);
        }
        wrapper.orderByAsc(RepairLog::getCreateTime);
        return repairLogMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void appendDescription(Integer orderId, String content) {
        User currentUser = TokenUtils.getCurrentUser();
        RepairOrder order = getOrderDetail(orderId);

        if (!order.getSubmitUserId().equals(currentUser.getId())) {
            throw new ServiceException("401", "无权操作此工单");
        }
        if (!"处理中".equals(order.getStatus())) {
            throw new ServiceException("500", "当前工单状态不允许追加描述");
        }

        RepairLog log = new RepairLog();
        log.setOrderId(orderId);
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setAction("追加");
        log.setContent(content);
        log.setIsVisibleToUser(true);
        log.setCreateTime(new Date());
        repairLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void confirmOrder(Integer orderId) {
        User currentUser = TokenUtils.getCurrentUser();
        RepairOrder order = getOrderDetail(orderId);

        if (!order.getSubmitUserId().equals(currentUser.getId())) {
            throw new ServiceException("401", "无权操作此工单");
        }
        if (!"待确认".equals(order.getStatus())) {
            throw new ServiceException("500", "当前工单状态不是待确认");
        }

        order.setStatus("已解决");
        order.setUpdateTime(new Date());
        repairOrderMapper.updateById(order);

        RepairLog log = new RepairLog();
        log.setOrderId(orderId);
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setAction("确认");
        log.setContent("用户确认问题已解决");
        log.setIsVisibleToUser(true);
        log.setCreateTime(new Date());
        repairLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void reopenOrder(Integer orderId, String reason) {
        User currentUser = TokenUtils.getCurrentUser();
        RepairOrder order = getOrderDetail(orderId);

        if (!order.getSubmitUserId().equals(currentUser.getId())) {
            throw new ServiceException("401", "无权操作此工单");
        }
        if (!"待确认".equals(order.getStatus())) {
            throw new ServiceException("500", "当前工单状态不是待确认");
        }

        order.setStatus("处理中");
        order.setUpdateTime(new Date());
        repairOrderMapper.updateById(order);

        RepairLog log = new RepairLog();
        log.setOrderId(orderId);
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setAction("退回");
        log.setContent("用户不满意处理结果，原因：" + reason);
        log.setIsVisibleToUser(true);
        log.setCreateTime(new Date());
        repairLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void deleteOrder(Integer orderId) {
        User currentUser = TokenUtils.getCurrentUser();
        RepairOrder order = getOrderDetail(orderId);

        if (!order.getSubmitUserId().equals(currentUser.getId())) {
            throw new ServiceException("401", "无权操作此工单");
        }
        if (!"已解决".equals(order.getStatus()) && !"已关闭".equals(order.getStatus())) {
            throw new ServiceException("500", "只能删除已解决或已关闭的工单");
        }

        repairOrderMapper.deleteById(orderId);
        LambdaQueryWrapper<RepairLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(RepairLog::getOrderId, orderId);
        repairLogMapper.delete(logWrapper);

        LambdaQueryWrapper<RepairEvaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(RepairEvaluation::getOrderId, orderId);
        repairEvaluationMapper.delete(evalWrapper);
    }

    @Override
    @Transactional
    public void assignOrder(Integer orderId, Integer assignUserId, String assignUserName) {
        RepairOrder order = getOrderDetail(orderId);
        if (!"待处理".equals(order.getStatus())) {
            throw new ServiceException("500", "只能分配待处理的工单");
        }

        User currentUser = TokenUtils.getCurrentUser();
        order.setAssignUserId(assignUserId);
        order.setAssignUserName(assignUserName);
        order.setStatus("处理中");
        order.setUpdateTime(new Date());
        repairOrderMapper.updateById(order);

        RepairLog log = new RepairLog();
        log.setOrderId(orderId);
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setAction("分配");
        log.setContent("工单已分配给：" + assignUserName);
        log.setIsVisibleToUser(true);
        log.setCreateTime(new Date());
        repairLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void claimOrder(Integer orderId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("401", "未登录");
        }

        RepairOrder order = getOrderDetail(orderId);
        if (!"待处理".equals(order.getStatus())) {
            throw new ServiceException("500", "只能认领待处理的工单");
        }

        order.setAssignUserId(currentUser.getId());
        order.setAssignUserName(currentUser.getNickname());
        order.setStatus("处理中");
        order.setUpdateTime(new Date());
        repairOrderMapper.updateById(order);

        RepairLog log = new RepairLog();
        log.setOrderId(orderId);
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setAction("认领");
        log.setContent("运维人员已认领该工单");
        log.setIsVisibleToUser(true);
        log.setCreateTime(new Date());
        repairLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void processOrder(Integer orderId, String status, String reply, String internalNote) {
        User currentUser = TokenUtils.getCurrentUser();
        RepairOrder order = getOrderDetail(orderId);

        order.setStatus(status);
        order.setUpdateTime(new Date());
        repairOrderMapper.updateById(order);

        if (StrUtil.isNotBlank(reply)) {
            RepairLog replyLog = new RepairLog();
            replyLog.setOrderId(orderId);
            replyLog.setOperatorId(currentUser.getId());
            replyLog.setOperatorName(currentUser.getNickname());
            replyLog.setAction("回复");
            replyLog.setContent(reply);
            replyLog.setIsVisibleToUser(true);
            replyLog.setCreateTime(new Date());
            repairLogMapper.insert(replyLog);
        }

        if (StrUtil.isNotBlank(internalNote)) {
            RepairLog noteLog = new RepairLog();
            noteLog.setOrderId(orderId);
            noteLog.setOperatorId(currentUser.getId());
            noteLog.setOperatorName(currentUser.getNickname());
            noteLog.setAction("内部备注");
            noteLog.setContent(internalNote);
            noteLog.setIsVisibleToUser(false);
            noteLog.setCreateTime(new Date());
            repairLogMapper.insert(noteLog);
        }
    }

    @Override
    public Page<RepairOrder> getPendingConfirmOrders(Integer pageNum, Integer pageSize) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("401", "未登录");
        }

        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getSubmitUserId, currentUser.getId());
        wrapper.eq(RepairOrder::getStatus, "待确认");
        wrapper.orderByDesc(RepairOrder::getUpdateTime);
        return repairOrderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void evaluateOrder(Integer orderId, Integer rating, String comment) {
        User currentUser = TokenUtils.getCurrentUser();
        RepairOrder order = getOrderDetail(orderId);

        if (!order.getSubmitUserId().equals(currentUser.getId())) {
            throw new ServiceException("401", "无权评价此工单");
        }
        if (!"已解决".equals(order.getStatus())) {
            throw new ServiceException("500", "只能评价已解决的工单");
        }

        LambdaQueryWrapper<RepairEvaluation> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(RepairEvaluation::getOrderId, orderId);
        if (repairEvaluationMapper.selectCount(existWrapper) > 0) {
            throw new ServiceException("500", "该工单已评价，请勿重复提交");
        }

        RepairEvaluation evaluation = new RepairEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setUserId(currentUser.getId());
        evaluation.setUserName(currentUser.getNickname());
        evaluation.setRating(rating);
        evaluation.setComment(comment);
        evaluation.setCreateTime(new Date());
        repairEvaluationMapper.insert(evaluation);

        order.setStatus("已关闭");
        order.setUpdateTime(new Date());
        repairOrderMapper.updateById(order);

        RepairLog log = new RepairLog();
        log.setOrderId(orderId);
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setAction("评价");
        log.setContent("用户评价：" + rating + "星" + (StrUtil.isNotBlank(comment) ? "，" + comment : ""));
        log.setIsVisibleToUser(true);
        log.setCreateTime(new Date());
        repairLogMapper.insert(log);
    }

    @Override
    public RepairEvaluation getEvaluation(Integer orderId) {
        LambdaQueryWrapper<RepairEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairEvaluation::getOrderId, orderId);
        return repairEvaluationMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", count());
        stats.put("pending", count(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, "待处理")));
        stats.put("processing", count(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, "处理中")));
        stats.put("pendingConfirm", count(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, "待确认")));
        stats.put("resolved", count(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, "已解决")));
        stats.put("closed", count(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, "已关闭")));
        return stats;
    }

    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        int seq = ORDER_SEQ.incrementAndGet();
        return "RO" + dateStr + String.format("%03d", seq % 1000);
    }
}
