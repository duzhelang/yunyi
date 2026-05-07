package com.oda.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.RepairEvaluation;
import com.oda.springboot.entity.RepairLog;
import com.oda.springboot.entity.RepairOrder;
import com.oda.springboot.entity.User;
import com.oda.springboot.mapper.UserMapper;
import com.oda.springboot.service.IRepairService;
import com.oda.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Resource
    private IRepairService repairService;

    @Resource
    private UserMapper userMapper;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @PostMapping("/submit")
    public Result submit(@RequestBody RepairOrder order) {
        try {
            RepairOrder saved = repairService.submitOrder(order);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my/list")
    public Result getMyList(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String status,
                            @RequestParam(defaultValue = "") String keyword) {
        try {
            Page<RepairOrder> page = repairService.getMyOrders(pageNum, pageSize, status, keyword);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/all/list")
    public Result getAllList(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String status,
                            @RequestParam(defaultValue = "") String urgency,
                            @RequestParam(defaultValue = "") String keyword) {
        try {
            Page<RepairOrder> page = repairService.getAllOrders(pageNum, pageSize, status, urgency, keyword);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result getDetail(@PathVariable Integer id) {
        try {
            RepairOrder order = repairService.getOrderDetail(id);
            User currentUser = TokenUtils.getCurrentUser();
            boolean onlyVisible = !"ROLE_ADMIN".equals(currentUser.getRole())
                    && !"ROLE_MAINTENANCE".equals(currentUser.getRole());
            List<RepairLog> logs = repairService.getOrderLogs(id, onlyVisible);
            Map<String, Object> data = new HashMap<>();
            data.put("order", order);
            data.put("logs", logs);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/append/{id}")
    public Result append(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            repairService.appendDescription(id, body.get("content"));
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/confirm/{id}")
    public Result confirm(@PathVariable Integer id) {
        try {
            repairService.confirmOrder(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/reopen/{id}")
    public Result reopen(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            repairService.reopenOrder(id, body.get("reason"));
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            repairService.deleteOrder(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/assign/{id}")
    public Result assign(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Integer assignUserId = (Integer) body.get("assignUserId");
            String assignUserName = (String) body.get("assignUserName");
            repairService.assignOrder(id, assignUserId, assignUserName);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/claim/{id}")
    public Result claim(@PathVariable Integer id) {
        try {
            repairService.claimOrder(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/process/{id}")
    public Result process(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            repairService.processOrder(id, body.get("status"), body.get("reply"), body.get("internalNote"));
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/pending/confirm")
    public Result getPendingConfirm(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        try {
            Page<RepairOrder> page = repairService.getPendingConfirmOrders(pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/evaluate/{id}")
    public Result evaluate(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Integer rating = (Integer) body.get("rating");
            String comment = (String) body.get("comment");
            repairService.evaluateOrder(id, rating, comment);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/evaluation/{id}")
    public Result getEvaluation(@PathVariable Integer id) {
        try {
            RepairEvaluation evaluation = repairService.getEvaluation(id);
            return Result.success(evaluation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public Result getStats() {
        try {
            return Result.success(repairService.getStats());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/users")
    public Result getUsers() {
        try {
            return Result.success(userMapper.selectList(null));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/upload")
    public Result uploadAttachment(@RequestParam MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;
            String repairDir = fileUploadPath + "repair" + File.separator;
            File uploadFile = new File(new File(repairDir).getAbsoluteFile(), uniqueFileName);
            File parentFile = uploadFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.transferTo(uploadFile);
            String url = "http://" + serverIp + ":9090/file/repair/" + uniqueFileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
