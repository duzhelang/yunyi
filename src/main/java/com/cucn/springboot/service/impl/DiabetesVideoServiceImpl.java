package com.cucn.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cucn.springboot.entity.DiabetesVideo;
import com.cucn.springboot.mapper.DiabetesVideoMapper;
import com.cucn.springboot.service.IDiabetesVideoService;
import org.springframework.stereotype.Service;

@Service
public class DiabetesVideoServiceImpl extends ServiceImpl<DiabetesVideoMapper, DiabetesVideo> implements IDiabetesVideoService {
}