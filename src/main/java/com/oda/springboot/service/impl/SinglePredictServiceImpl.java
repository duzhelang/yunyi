package com.oda.springboot.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.ModelVersion;
import com.oda.springboot.service.ISinglePredictService;
import com.oda.springboot.service.ModelVersionService;
import com.oda.springboot.utils.PropertyUtil;
import com.oda.springboot.utils.UsePythonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class SinglePredictServiceImpl implements ISinglePredictService {

	private static final Logger log = LoggerFactory.getLogger(SinglePredictServiceImpl.class);

	@Autowired
	private PropertyUtil propertyUtil;

	@Autowired
	private ModelVersionService modelVersionService;

	@Value("${base.path:./}")
	private String basePath;

	@Override
	public Result<Map<String, Object>> singlePredict(Map<String, Object> requestData) {
		try {
			double pregnancies = getDoubleValue(requestData, "pregnancies", 0.0);
			double glucose = getDoubleValue(requestData, "glucose", 0.0);
			double bloodPressure = getDoubleValue(requestData, "bloodPressure", 0.0);
			double skinThickness = getDoubleValue(requestData, "skinThickness", 0.0);
			double insulin = getDoubleValue(requestData, "insulin", 0.0);
			double bmi = getDoubleValue(requestData, "bmi", 0.0);
			double diabetesPedigreeFunction = getDoubleValue(requestData, "diabetesPedigreeFunction", 0.0);
			double age = getDoubleValue(requestData, "age", 0.0);

			if (!validateInput(pregnancies, glucose, bloodPressure, skinThickness, insulin, bmi, diabetesPedigreeFunction, age)) {
				return Result.error("400", "输入参数超出有效范围");
			}

			String pythonExe = propertyUtil.getPythonExe();
			String pythonScript = propertyUtil.getPythonPath() + "predict_single.py";

			if (!checkPythonEnvironment(pythonExe, pythonScript)) {
				return Result.error("503", "Python环境未就绪，请联系管理员");
			}

			String modelBasePath = getActiveModelBasePath();

			String[] arguments = new String[]{
					pythonExe,
					pythonScript,
					String.valueOf(pregnancies),
					String.valueOf(glucose),
					String.valueOf(bloodPressure),
					String.valueOf(skinThickness),
					String.valueOf(insulin),
					String.valueOf(bmi),
					String.valueOf(diabetesPedigreeFunction),
					String.valueOf(age),
					"--model",
					modelBasePath
			};

			String output = callPythonWithTimeout(arguments, 30);

			if (output == null) {
				return Result.error("504", "检测超时");
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode resultNode = mapper.readTree(output);

			String status = resultNode.get("status").asText();
			if ("success".equals(status)) {
				int prediction = resultNode.get("prediction").asInt();
				double probability = resultNode.get("probability").asDouble();

				Map<String, Object> resultData = new HashMap<>();
				resultData.put("prediction", prediction);
				resultData.put("probability", probability);
				resultData.put("features", resultNode.get("features"));
				resultData.put("time", resultNode.get("time").asText());

				if (resultNode.has("risk_level")) {
					resultData.put("risk_level", resultNode.get("risk_level").asText());
				}
				if (resultNode.has("confidence_interval")) {
					resultData.put("confidence_interval", mapper.convertValue(resultNode.get("confidence_interval"), double[].class));
				}
				if (resultNode.has("feature_names")) {
					resultData.put("feature_names", mapper.convertValue(resultNode.get("feature_names"), String[].class));
				}
				if (resultNode.has("feature_importance")) {
					resultData.put("feature_importance", mapper.convertValue(resultNode.get("feature_importance"), double[].class));
				}
				if (resultNode.has("percentiles")) {
					resultData.put("percentiles", mapper.convertValue(resultNode.get("percentiles"), Map.class));
				}
				if (resultNode.has("similar_cases")) {
					resultData.put("similar_cases", mapper.convertValue(resultNode.get("similar_cases"), Map.class));
				}
				if (resultNode.has("charts")) {
					resultData.put("charts", mapper.convertValue(resultNode.get("charts"), Map.class));
				}

				return Result.success("检测成功", resultData);
			} else {
				String msg = resultNode.get("msg").asText();
				return Result.error("500", "检测失败: " + msg);
			}

		} catch (IllegalArgumentException e) {
			return Result.error("400", e.getMessage());
		} catch (IOException | InterruptedException e) {
			return Result.error("500", "检测失败: " + e.getMessage());
		} catch (Exception e) {
			return Result.error("500", "检测失败: " + e.getMessage());
		}
	}

	private boolean validateInput(double pregnancies, double glucose, double bloodPressure, double skinThickness, double insulin, double bmi, double diabetesPedigreeFunction, double age) {
		if (pregnancies < 0 || pregnancies > 20) return false;
		if (glucose < 0 || glucose > 300) return false;
		if (bloodPressure < 0 || bloodPressure > 200) return false;
		if (skinThickness < 0 || skinThickness > 100) return false;
		if (insulin < 0 || insulin > 1000) return false;
		if (bmi < 0 || bmi > 100) return false;
		if (diabetesPedigreeFunction < 0 || diabetesPedigreeFunction > 3) return false;
		if (age < 0 || age > 150) return false;
		return true;
	}

	private boolean checkPythonEnvironment(String pythonExe, String scriptPath) {
		File pythonFile = new File(pythonExe);
		if (!pythonFile.exists()) {
			return false;
		}
		File scriptFile = new File(scriptPath);
		return scriptFile.exists();
	}

	private String callPythonWithTimeout(final String[] arguments, int timeoutSeconds) throws IOException, InterruptedException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(() -> {
			try {
				return UsePythonUtils.callPythonAndGetOutput(arguments);
			} catch (IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		});

		try {
			return future.get(timeoutSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			future.cancel(true);
			return null;
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof IOException) {
				throw (IOException) cause;
			} else if (cause instanceof InterruptedException) {
				throw (InterruptedException) cause;
			}
			throw new RuntimeException(cause);
		} finally {
			executor.shutdown();
		}
	}

	private double getDoubleValue(Map<String, Object> data, String key, double defaultValue) {
		Object value = data.get(key);
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		if (value instanceof String) {
			try {
				return Double.parseDouble((String) value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	private String getActiveModelBasePath() {
		try {
			ModelVersion activeModel = modelVersionService.getActiveModel();
			if (activeModel != null && activeModel.getFilePath() != null) {
				String filePath = activeModel.getFilePath();
				if (filePath.endsWith(".pth")) {
					filePath = filePath.substring(0, filePath.length() - 4);
				}
				if (!new File(filePath).isAbsolute()) {
					filePath = basePath + filePath;
				}
				return filePath;
			}
		} catch (Exception e) {
			// 忽略异常，使用默认路径
		}
		return basePath + "python/diabetes_model";
	}
}
