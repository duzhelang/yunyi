package com.oda.springboot.mapper;

import com.oda.springboot.entity.EducationComment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface EducationCommentMapper {

    /**
     * 查询所有留言（按创建时间倒序）
     */
    List<EducationComment> selectAll();

    /**
     * 根据ID查询单条留言
     */
    EducationComment selectById(Integer id);

    /**
     * 新增留言
     */
    int insert(EducationComment comment);

    /**
     * 根据ID删除留言
     */
    int deleteById(Integer id);
}
