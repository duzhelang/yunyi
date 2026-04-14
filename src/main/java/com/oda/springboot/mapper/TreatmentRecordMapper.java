package com.oda.springboot.mapper;

import com.oda.springboot.entity.TreatmentRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface TreatmentRecordMapper {

    /**
     * 多条件分页查询诊疗档?
     */
    List<TreatmentRecord> selectList(@Param("query") TreatmentRecord query);

    /**
     * 新增单条诊疗档案
     */
    int insert(TreatmentRecord record);

    /**
     * 批量新增诊疗档案(Excel导入用)
     */
    int batchInsert(@Param("list") List<TreatmentRecord> list);

    /**
     * 根据ID修改诊疗档案
     */
    int updateById(TreatmentRecord record);

    /**
     * 根据ID删除诊疗档案
     */
    int deleteById(Long id);

    /**
     * 根据身份证号查询(用于重复校验)
     */
    TreatmentRecord selectByIdCard(String idCard);
}