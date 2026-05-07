package com.oda.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.oda.springboot.entity.RepairEvaluation;
import com.oda.springboot.entity.RepairLog;
import com.oda.springboot.entity.RepairOrder;

import java.util.List;
import java.util.Map;

public interface IRepairService extends IService<RepairOrder> {

    RepairOrder submitOrder(RepairOrder order);

    Page<RepairOrder> getMyOrders(Integer pageNum, Integer pageSize, String status, String keyword);

    Page<RepairOrder> getAllOrders(Integer pageNum, Integer pageSize, String status, String urgency, String keyword);

    RepairOrder getOrderDetail(Integer id);

    List<RepairLog> getOrderLogs(Integer orderId, boolean onlyVisible);

    void appendDescription(Integer orderId, String content);

    void confirmOrder(Integer orderId);

    void reopenOrder(Integer orderId, String reason);

    void deleteOrder(Integer orderId);

    void assignOrder(Integer orderId, Integer assignUserId, String assignUserName);

    void claimOrder(Integer orderId);

    void processOrder(Integer orderId, String status, String reply, String internalNote);

    Page<RepairOrder> getPendingConfirmOrders(Integer pageNum, Integer pageSize);

    void evaluateOrder(Integer orderId, Integer rating, String comment);

    RepairEvaluation getEvaluation(Integer orderId);

    Map<String, Object> getStats();
}
