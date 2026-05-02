package com.oda.springboot.service.impl;

import com.oda.springboot.entity.EducationComment;
import com.oda.springboot.mapper.EducationCommentMapper;
import com.oda.springboot.service.IEducationCommentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EducationCommentServiceImpl implements IEducationCommentService {

    private final EducationCommentMapper educationCommentMapper;

    public EducationCommentServiceImpl(EducationCommentMapper educationCommentMapper) {
        this.educationCommentMapper = educationCommentMapper;
    }

    @Override
    public List<EducationComment> getAll() {
        return educationCommentMapper.selectAll();
    }

    @Override
    public EducationComment getById(Integer id) {
        return educationCommentMapper.selectById(id);
    }

    @Override
    public void add(EducationComment comment) {
        educationCommentMapper.insert(comment);
    }

    @Override
    public void delete(Integer id) {
        educationCommentMapper.deleteById(id);
    }
}
