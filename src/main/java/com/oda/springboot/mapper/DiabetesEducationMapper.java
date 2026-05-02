package com.oda.springboot.mapper;

import com.oda.springboot.entity.DiabetesEducation;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface DiabetesEducationMapper {

    /**
     * 查询所有科普内容（按排序顺序）
     */
    List<DiabetesEducation> selectAll();

    /**
     * 根据ID查询单条科普内容
     */
    DiabetesEducation selectById(Integer id);

    /**
     * 根据篇章ID查询
     */
    DiabetesEducation selectBySectionId(@Param("sectionId") Integer sectionId);

    /**
     * 新增科普内容
     */
    int insert(DiabetesEducation education);

    /**
     * 根据ID修改科普内容
     */
    int updateById(DiabetesEducation education);

    /**
     * 根据ID删除科普内容
     */
    int deleteById(Integer id);
}
