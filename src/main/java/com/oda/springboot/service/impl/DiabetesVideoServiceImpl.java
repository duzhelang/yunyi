package com.oda.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oda.springboot.entity.DiabetesVideo;
import com.oda.springboot.mapper.DiabetesVideoMapper;
import com.oda.springboot.service.IDiabetesVideoService;
import org.springframework.stereotype.Service;

@Service
public class DiabetesVideoServiceImpl extends ServiceImpl<DiabetesVideoMapper, DiabetesVideo> implements IDiabetesVideoService {
}