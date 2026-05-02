package com.oda.springboot.service;

import com.oda.springboot.entity.DiabetesEducation;
import java.util.List;

public interface IDiabetesEducationService {

    /**
     * 获取所有科普内容
     */
    List<DiabetesEducation> getAll();

    /**
     * 根据ID获取单条科普内容
     */
    DiabetesEducation getById(Integer id);

    /**
     * 根据篇章ID获取科普内容
     */
    DiabetesEducation getBySectionId(Integer sectionId);

    /**
     * 新增科普内容
     */
    void add(DiabetesEducation education);

    /**
     * 修改科普内容
     */
    void update(DiabetesEducation education);

    /**
     * 删除科普内容
     */
    void delete(Integer id);
}
