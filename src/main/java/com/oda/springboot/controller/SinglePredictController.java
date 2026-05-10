package com.oda.springboot.controller;

import com.oda.springboot.common.Result;
import com.oda.springboot.service.ISinglePredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/predict")
public class SinglePredictController {

    @Autowired
    private ISinglePredictService singlePredictService;

    @PostMapping("/single")
    public Result<Map<String, Object>> singlePredict(@RequestBody Map<String, Object> requestData) {
        return singlePredictService.singlePredict(requestData);
    }
}
