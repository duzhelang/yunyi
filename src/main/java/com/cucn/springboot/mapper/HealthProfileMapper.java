package com.cucn.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cucn.springboot.entity.HealthProfile;

/**
 * 健康档案 Mapper 接口
 * 继承 BaseMapper 后,无需编写 XML 即可使用 insert, selectById, updateById 等方?
 */
public interface HealthProfileMapper extends BaseMapper<HealthProfile> {
    // 不需要任何额外代?
    // 如果需要复?SQL (如多表联?,可在此定义方法并在 resources/mapper 下写 XML
}