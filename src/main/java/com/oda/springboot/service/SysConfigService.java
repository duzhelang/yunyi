package com.oda.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oda.springboot.entity.SysConfig;
import com.oda.springboot.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> {

    public String getDefaultModel() {
        SysConfig config = baseMapper.selectById("default_ai_model");
        return (config == null || config.getConfigValue().isBlank()) ? "glm-4-flash" : config.getConfigValue();
    }

    public void setDefaultModel(String model) {
        SysConfig config = new SysConfig();
        config.setConfigKey("default_ai_model");
        config.setConfigValue(model);
        saveOrUpdate(config);
    }
}