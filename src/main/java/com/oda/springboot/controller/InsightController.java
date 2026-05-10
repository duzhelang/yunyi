package com.oda.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.AnalysisRecord;
import com.oda.springboot.entity.HealthProfile;
import com.oda.springboot.entity.PredictionRecord;
import com.oda.springboot.entity.User;
import com.oda.springboot.mapper.AnalysisRecordMapper;
import com.oda.springboot.mapper.PredictionRecordMapper;
import com.oda.springboot.service.ISinglePredictService;
import com.oda.springboot.service.impl.HealthProfileServiceImpl;
import com.oda.springboot.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 个体洞察控制器
 * 提供个体健康预测洞察功能及洞察记录查询
 */
@RestController
@RequestMapping("/api/insight")
public class InsightController {

    private static final Logger log = LoggerFactory.getLogger(InsightController.class);

    @Autowired
    private HealthProfileServiceImpl healthProfileService;

    @Autowired
    private PredictionRecordMapper predictionRecordMapper;

    @Autowired
    private ISinglePredictService singlePredictService;

    @Autowired
    private AnalysisRecordMapper analysisRecordMapper;

    /**
     * 获取个体预测洞察
     * 对单个患者档案进行预测分析，并自动保存洞察记录
     */
    @GetMapping("/{healthProfileId}")
    public Result getInsight(@PathVariable Long healthProfileId) {
        try {
            log.info("开始个体预测洞察，profileId={}", healthProfileId);

            HealthProfile profile = healthProfileService.getById(healthProfileId);
            if (profile == null) {
                return Result.error("健康档案不存在");
            }

            Map<String, Object> features = new HashMap<>();
            features.put("pregnancies", profile.getPregnancies() != null ? profile.getPregnancies() : 0);
            features.put("glucose", profile.getGlucose() != null ? profile.getGlucose() : 0.0);
            features.put("bloodPressure", profile.getBloodPressure() != null ? profile.getBloodPressure() : 0);
            features.put("skinThickness", profile.getSkinThickness() != null ? profile.getSkinThickness() : 0);
            features.put("insulin", profile.getInsulin() != null ? profile.getInsulin() : 0.0);
            features.put("bmi", profile.getBMI() != null ? profile.getBMI() : 0.0);
            features.put("diabetesPedigreeFunction", profile.getDiabetesPedigreeFunction() != null ? profile.getDiabetesPedigreeFunction() : 0.0);
            features.put("age", profile.getAge() != null ? profile.getAge() : 0);

            Result predictResult = singlePredictService.singlePredict(features);
            if (!"200".equals(predictResult.getCode())) {
                return Result.error("预测失败: " + predictResult.getMsg());
            }

            Map<String, Object> predictionData = (Map<String, Object>) predictResult.getData();

            double probability = predictionData.get("probability") instanceof Number
                        ? ((Number) predictionData.get("probability")).doubleValue()
                        : 0.0;
            String riskLevel = (String) predictionData.getOrDefault("risk_level", "low");

            String[] ci = null;
            if (predictionData.containsKey("confidence_interval")) {
                Object ciObj = predictionData.get("confidence_interval");
                if (ciObj instanceof double[]) {
                    double[] ciArr = (double[]) ciObj;
                    ci = new String[]{String.valueOf(ciArr[0] * 100), String.valueOf(ciArr[1] * 100)};
                }
            }

            savePredictionRecord(healthProfileId, probability, riskLevel, ci, predictionData, profile);

            List<Map<String, Object>> similarCases = findSimilarCases(probability, healthProfileId);

            Map<String, Object> result = new HashMap<>();
            result.put("probability", probability);
            result.put("riskLevel", riskLevel);
            result.put("confidenceInterval", ci);
            result.put("featureImportance", predictionData.get("feature_importance"));
            result.put("featureNames", predictionData.get("feature_names"));
            result.put("percentiles", predictionData.get("percentiles"));
            result.put("charts", predictionData.get("charts"));
            result.put("similarCases", similarCases);
            result.put("profile", buildProfileSummary(profile));

            // 保存个体洞察记录并获取记录ID
            Long recordId = saveIndividualInsightRecord(healthProfileId, result);
            result.put("analysisRecordId", recordId);

            log.info("个体预测洞察完成，probability={}, riskLevel={}", probability, riskLevel);
            return Result.success(result);

        } catch (Exception e) {
            log.error("个体洞察异常", e);
            return Result.error("个体洞察失败: " + e.getMessage());
        }
    }

    /**
     * 查询当前用户的个体洞察历史记录
     * @param limit 返回记录数量上限，默认10条
     */
    @GetMapping("/history")
    public Result getHistory(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }

            QueryWrapper<AnalysisRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", currentUser.getId().longValue())
                   .eq("analysis_type", "individual")
                   .orderByDesc("analysis_time")
                   .last("LIMIT " + limit);

            List<AnalysisRecord> records = analysisRecordMapper.selectList(wrapper);
            return Result.success(records);
        } catch (Exception e) {
            log.error("查询个体洞察历史失败", e);
            return Result.error("查询个体洞察历史失败: " + e.getMessage());
        }
    }

    /**
     * 保存个体洞察记录到数据库
     * @param healthProfileId 患者档案ID
     * @param resultData 洞察结果数据
     * @return 保存的记录ID，失败返回null
     */
    private Long saveIndividualInsightRecord(Long healthProfileId, Map<String, Object> resultData) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                log.warn("保存个体洞察记录失败：未获取到当前用户");
                return null;
            }

            AnalysisRecord record = new AnalysisRecord();
            record.setAnalysisType("individual");
            record.setUserId(currentUser.getId().longValue());
            record.setProfileIds(new ObjectMapper().writeValueAsString(Collections.singletonList(healthProfileId)));
            record.setResultData(new ObjectMapper().writeValueAsString(resultData));

            analysisRecordMapper.insert(record);
            log.info("个体洞察记录保存成功，userId={}, profileId={}, recordId={}", currentUser.getId(), healthProfileId, record.getId());
            return record.getId();
        } catch (Exception e) {
            log.warn("保存个体洞察记录失败", e);
            return null;
        }
    }

    private void savePredictionRecord(Long profileId, Double probability, String riskLevel,
                                       String[] ci, Map<String, Object> predictionData, HealthProfile profile) {
        try {
            PredictionRecord record = new PredictionRecord();
            record.setHealthProfileId(profileId);
            record.setProbability(probability);
            record.setRiskLevel(riskLevel);
            if (ci != null) {
                record.setConfidenceInterval(ci[0] + "," + ci[1]);
            }
            if (predictionData.containsKey("feature_importance")) {
                record.setFeatureImportance(new ObjectMapper().writeValueAsString(predictionData.get("feature_importance")));
            }
            record.setAge(profile.getAge());
            record.setGender(profile.getGender());
            record.setPredictedAt(java.time.LocalDateTime.now());
            predictionRecordMapper.insert(record);
        } catch (Exception e) {
            log.warn("保存预测记录失败", e);
        }
    }

    private List<Map<String, Object>> findSimilarCases(double probability, Long excludeId) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            double lower = Math.max(0, probability - 10);
            double upper = Math.min(100, probability + 10);

            QueryWrapper<PredictionRecord> wrapper = new QueryWrapper<>();
            wrapper.ne("health_profile_id", excludeId)
                   .between("probability", lower, upper)
                   .orderByDesc("predicted_at")
                   .last("LIMIT 5");

            List<PredictionRecord> records = predictionRecordMapper.selectList(wrapper);
            for (PredictionRecord record : records) {
                Map<String, Object> caseData = new HashMap<>();
                caseData.put("id", record.getHealthProfileId());
                caseData.put("probability", record.getProbability());
                caseData.put("riskLevel", record.getRiskLevel());
                caseData.put("age", record.getAge());
                caseData.put("gender", record.getGender());
                caseData.put("predictedAt", record.getPredictedAt());
                result.add(caseData);
            }
        } catch (Exception e) {
            log.warn("查询相似病例失败", e);
        }
        return result;
    }

    private Map<String, Object> buildProfileSummary(HealthProfile profile) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", profile.getId());
        summary.put("glucose", profile.getGlucose());
        summary.put("bmi", profile.getBMI());
        summary.put("age", profile.getAge());
        summary.put("bloodPressure", profile.getBloodPressure());
        summary.put("insulin", profile.getInsulin());
        summary.put("pregnancies", profile.getPregnancies());
        summary.put("gender", profile.getGender());
        return summary;
    }
}
