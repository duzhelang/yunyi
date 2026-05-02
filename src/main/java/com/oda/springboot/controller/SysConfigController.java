package com.oda.springboot.controller;

/**
 * @BelongsProject: Software-ODA
 * @BelongsPackage: com.oda.springboot.controller
 * @Author: ODA-cj
 * @CreateTime: 2026/4/30 下午9:13
 * @Version: 1.0
 */

import com.oda.springboot.common.Result;
import com.oda.springboot.config.AuthAccess;
import com.oda.springboot.entity.User;
import com.oda.springboot.service.SysConfigService;
import com.oda.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    // 支持的模型列表
    private static final List<String> VALID_MODELS = Arrays.asList("glm-4-flash", "glm-4.7-flash", "deepseek", "kimi", "mimo-v2.5-pro", "mimo-v2-flash");

    /**
     * 获取当前默认AI模型（所有用户可访问，包括未登录）
     */
    @AuthAccess
    @GetMapping("/defaultModel")
    public Result<String> getDefaultModel() {
        return Result.success(sysConfigService.getDefaultModel());
    }

    /**
     * 设置默认AI模型（仅管理员）
     * 权限验证可通过拦截器/注解实现，此处简化处理，实际项目中请结合你的权限框架
     */
    @PostMapping("/defaultModel")
    public Result<?> setDefaultModel(@RequestParam String model) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return Result.error("无权限操作，仅管理员可切换默认模型");
        }
        if (!VALID_MODELS.contains(model)) {
            return Result.error("不支持的模型，可选值：" + VALID_MODELS);
        }
        sysConfigService.setDefaultModel(model);
        return Result.success("默认模型已更新为：" + model);
    }
}