package com.oda.springboot.service;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oda.springboot.entity.ModelVersion;
import com.oda.springboot.mapper.ModelVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelVersionService extends ServiceImpl<ModelVersionMapper, ModelVersion> {

    private static final Log log = Log.get();

    @Autowired
    private ModelVersionMapper modelVersionMapper;

    /**
     * 获取所有模型列表
     */
    public List<ModelVersion> getAllModels() {
        LambdaQueryWrapper<ModelVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ModelVersion::getCreateTime);
        return modelVersionMapper.selectList(queryWrapper);
    }

    /**
     * 获取当前激活的模型
     */
    public ModelVersion getActiveModel() {
        LambdaQueryWrapper<ModelVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ModelVersion::getStatus, "active");
        List<ModelVersion> list = modelVersionMapper.selectList(queryWrapper);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 激活指定模型
     */
    @Transactional
    public boolean activateModel(Integer modelId) {
        ModelVersion model = modelVersionMapper.selectById(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }

        // 先将该模型名下的其他版本都设置为inactive
        LambdaQueryWrapper<ModelVersion> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ModelVersion::getModelName, model.getModelName());
        updateWrapper.eq(ModelVersion::getStatus, "active");
        List<ModelVersion> activeList = modelVersionMapper.selectList(updateWrapper);
        
        for (ModelVersion activeModel : activeList) {
            activeModel.setStatus("inactive");
            modelVersionMapper.updateById(activeModel);
        }

        // 激活当前模型
        model.setStatus("active");
        model.setUpdateTime(new Date());
        int result = modelVersionMapper.updateById(model);
        
        log.info("模型已激活: {} - {}", model.getModelName(), model.getVersion());
        return result > 0;
    }

    /**
     * 取消激活指定模型
     */
    public boolean deactivateModel(Integer modelId) {
        ModelVersion model = modelVersionMapper.selectById(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }

        if (!"active".equals(model.getStatus())) {
            throw new RuntimeException("该模型未处于激活状态");
        }

        model.setStatus("inactive");
        model.setUpdateTime(new Date());
        int result = modelVersionMapper.updateById(model);
        
        log.info("模型已取消激活: {} - {}", model.getModelName(), model.getVersion());
        return result > 0;
    }

    /**
     * 删除模型
     */
    public boolean deleteModel(Integer modelId) {
        ModelVersion model = modelVersionMapper.selectById(modelId);
        if (model != null && "active".equals(model.getStatus())) {
            throw new RuntimeException("激活状态的模型不能删除");
        }
        return modelVersionMapper.deleteById(modelId) > 0;
    }

    /**
     * 新增模型
     */
    public boolean addModel(ModelVersion modelVersion) {
        modelVersion.setStatus("inactive");
        modelVersion.setCreateTime(new Date());
        return modelVersionMapper.insert(modelVersion) > 0;
    }

    /**
     * 更新模型信息
     */
    public boolean updateModel(ModelVersion modelVersion) {
        modelVersion.setUpdateTime(new Date());
        return modelVersionMapper.updateById(modelVersion) > 0;
    }

    /**
     * 获取模型详细信息
     */
    public ModelVersion getModelById(Integer modelId) {
        return modelVersionMapper.selectById(modelId);
    }

    /**
     * 获取激活模型的调用信息
     */
    public Map<String, Object> getActiveModelInfo() {
        Map<String, Object> result = new HashMap<>();
        
        ModelVersion activeModel = getActiveModel();
        if (activeModel != null) {
            result.put("modelId", activeModel.getId());
            result.put("modelName", activeModel.getModelName());
            result.put("version", activeModel.getVersion());
            result.put("filePath", activeModel.getFilePath());
            result.put("scalerPath", activeModel.getScalerPath());
            result.put("encoderPath", activeModel.getEncoderPath());
            result.put("description", activeModel.getDescription());
            result.put("status", "success");
        } else {
            result.put("status", "error");
            result.put("message", "没有激活的模型");
        }
        
        return result;
    }
}
