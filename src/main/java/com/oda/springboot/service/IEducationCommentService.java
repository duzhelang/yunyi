package com.oda.springboot.service;

import com.oda.springboot.entity.EducationComment;
import java.util.List;

public interface IEducationCommentService {

    /**
     * 获取所有留言
     */
    List<EducationComment> getAll();

    /**
     * 根据ID获取单条留言
     */
    EducationComment getById(Integer id);

    /**
     * 新增留言
     */
    void add(EducationComment comment);

    /**
     * 删除留言
     */
    void delete(Integer id);
}
