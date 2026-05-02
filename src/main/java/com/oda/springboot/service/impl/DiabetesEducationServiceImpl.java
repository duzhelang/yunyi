package com.oda.springboot.service.impl;

import com.oda.springboot.entity.DiabetesEducation;
import com.oda.springboot.mapper.DiabetesEducationMapper;
import com.oda.springboot.service.IDiabetesEducationService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiabetesEducationServiceImpl implements IDiabetesEducationService {

    private final DiabetesEducationMapper diabetesEducationMapper;

    public DiabetesEducationServiceImpl(DiabetesEducationMapper diabetesEducationMapper) {
        this.diabetesEducationMapper = diabetesEducationMapper;
    }

    @Override
    public List<DiabetesEducation> getAll() {
        return diabetesEducationMapper.selectAll();
    }

    @Override
    public DiabetesEducation getById(Integer id) {
        return diabetesEducationMapper.selectById(id);
    }

    @Override
    public DiabetesEducation getBySectionId(Integer sectionId) {
        return diabetesEducationMapper.selectBySectionId(sectionId);
    }

    @Override
    public void add(DiabetesEducation education) {
        diabetesEducationMapper.insert(education);
    }

    @Override
    public void update(DiabetesEducation education) {
        diabetesEducationMapper.updateById(education);
    }

    @Override
    public void delete(Integer id) {
        diabetesEducationMapper.deleteById(id);
    }
}
