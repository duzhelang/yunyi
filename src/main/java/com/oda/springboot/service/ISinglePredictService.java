package com.oda.springboot.service;

import com.oda.springboot.common.Result;
import java.util.Map;

public interface ISinglePredictService {

	Result<Map<String, Object>> singlePredict(Map<String, Object> requestData);
}
