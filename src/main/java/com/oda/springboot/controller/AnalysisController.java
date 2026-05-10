package com.oda.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.AnalysisRecord;
import com.oda.springboot.entity.HealthProfile;
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
 * 分析控制器
 * 提供群体分析功能及分析记录管理
 */
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    @Autowired
    private HealthProfileServiceImpl healthProfileService;

    @Autowired
    private PredictionRecordMapper predictionRecordMapper;

    @Autowired
    private ISinglePredictService singlePredictService;

    @Autowired
    private AnalysisRecordMapper analysisRecordMapper;

    /**
     * 群体分析接口
     * 对多个患者档案进行批量预测分析，并自动保存分析记录
     */
    @PostMapping("/group")
    public Result groupAnalysis(@RequestBody Map<String, Object> request) {
        try {
            List<?> profileIdsRaw = (List<?>) request.get("profileIds");
            if (profileIdsRaw == null || profileIdsRaw.isEmpty()) {
                return Result.error("请选择至少一个患者");
            }

            List<Long> profileIds = new ArrayList<>();
            for (Object id : profileIdsRaw) {
                profileIds.add(Long.valueOf(id.toString()));
            }

            log.info("开始群体分析，患者数量={}", profileIds.size());

            Map<String, Integer> riskDistribution = new HashMap<>();
            riskDistribution.put("low", 0);
            riskDistribution.put("medium", 0);
            riskDistribution.put("high", 0);

            double totalConfidence = 0;
            int validCount = 0;

            Map<String, Double> featureRanking = new LinkedHashMap<>();
            String[] featureNames = {"Pregnancies", "Glucose", "BloodPressure", "SkinThickness",
                    "Insulin", "BMI", "DiabetesPedigreeFunction", "Age"};
            for (String name : featureNames) {
                featureRanking.put(name, 0.0);
            }

            List<Map<String, Object>> patientResults = new ArrayList<>();

            for (Long profileId : profileIds) {
                HealthProfile profile = healthProfileService.getById(profileId);
                if (profile == null) continue;

                Map<String, Object> features = new HashMap<>();
                features.put("pregnancies", profile.getPregnancies() != null ? profile.getPregnancies() : 0);
                features.put("glucose", profile.getGlucose() != null ? profile.getGlucose() : 0.0);
                features.put("bloodPressure", profile.getBloodPressure() != null ? profile.getBloodPressure() : 0);
                features.put("skinThickness", profile.getSkinThickness() != null ? profile.getSkinThickness() : 0);
                features.put("insulin", profile.getInsulin() != null ? profile.getInsulin() : 0.0);
                features.put("bmi", profile.getBMI() != null ? profile.getBMI() : 0.0);
                features.put("diabetesPedigreeFunction", profile.getDiabetesPedigreeFunction() != null ? profile.getDiabetesPedigreeFunction() : 0.0);
                features.put("age", profile.getAge() != null ? profile.getAge() : 0);

                try {
                    Result predictResult = singlePredictService.singlePredict(features);
                    if ("200".equals(predictResult.getCode()) && predictResult.getData() != null) {
                        Map<String, Object> data = (Map<String, Object>) predictResult.getData();
                        String riskLevel = (String) data.getOrDefault("risk_level", "low");
                        double probability = data.get("probability") instanceof Number
                    ? ((Number) data.get("probability")).doubleValue()
                    : 0.0;

                        riskDistribution.merge(riskLevel, 1, Integer::sum);

                        if (data.containsKey("feature_importance")) {
                            Object fiObj = data.get("feature_importance");
                            if (fiObj instanceof double[]) {
                                double[] fiArr = (double[]) fiObj;
                                for (int i = 0; i < Math.min(fiArr.length, featureNames.length); i++) {
                                    featureRanking.merge(featureNames[i], fiArr[i], Double::sum);
                                }
                            }
                        }

                        totalConfidence += probability;
                        validCount++;

                        Map<String, Object> patientResult = new HashMap<>();
                        patientResult.put("profileId", profileId);
                        patientResult.put("probability", probability);
                        patientResult.put("riskLevel", riskLevel);
                        patientResult.put("age", profile.getAge());
                        patientResult.put("gender", profile.getGender());
                        patientResults.add(patientResult);
                    }
                } catch (Exception e) {
                    log.warn("患者{}预测失败", profileId, e);
                }
            }

            if (validCount > 0) {
                for (Map.Entry<String, Double> entry : featureRanking.entrySet()) {
                    entry.setValue(entry.getValue() / validCount);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("riskDistribution", riskDistribution);
            result.put("avgConfidence", validCount > 0 ? totalConfidence / validCount : 0);
            result.put("featureRanking", featureRanking);
            result.put("patientResults", patientResults);
            result.put("totalCount", profileIds.size());
            result.put("validCount", validCount);

            log.info("群体分析完成，有效样本={}/{}", validCount, profileIds.size());

            // 保存群体分析记录并获取记录ID
            Long recordId = saveGroupAnalysisRecord(profileIds, result);
            result.put("analysisRecordId", recordId);

            return Result.success(result);

        } catch (Exception e) {
            log.error("群体分析异常", e);
            return Result.error("群体分析失败: " + e.getMessage());
        }
    }

    /**
     * 查询当前用户的群体分析历史记录
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
                   .eq("analysis_type", "group")
                   .orderByDesc("analysis_time")
                   .last("LIMIT " + limit);

            List<AnalysisRecord> records = analysisRecordMapper.selectList(wrapper);
            return Result.success(records);
        } catch (Exception e) {
            log.error("查询分析历史失败", e);
            return Result.error("查询分析历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定分析记录详情
     * @param id 记录ID
     */
    @GetMapping("/record/{id}")
    public Result getRecordById(@PathVariable Long id) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }

            AnalysisRecord record = analysisRecordMapper.selectById(id);
            if (record == null) {
                return Result.error("记录不存在");
            }
            // 验证记录归属，仅允许查看自己的记录
            if (!record.getUserId().equals(currentUser.getId().longValue())) {
                return Result.error("403", "无权查看该记录");
            }

            return Result.success(record);
        } catch (Exception e) {
            log.error("查询分析记录详情失败", e);
            return Result.error("查询分析记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 删除指定分析记录
     * @param id 记录ID
     */
    @DeleteMapping("/record/{id}")
    public Result deleteRecord(@PathVariable Long id) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }

            AnalysisRecord record = analysisRecordMapper.selectById(id);
            if (record == null) {
                return Result.error("记录不存在");
            }
            // 验证记录归属，仅允许删除自己的记录
            if (!record.getUserId().equals(currentUser.getId().longValue())) {
                return Result.error("403", "无权删除该记录");
            }

            analysisRecordMapper.deleteById(id);
            log.info("删除分析记录成功，id={}", id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除分析记录失败", e);
            return Result.error("删除分析记录失败: " + e.getMessage());
        }
    }

    /**
     * 保存群体分析记录到数据库
     * @param profileIds 分析的档案ID列表
     * @param resultData 分析结果数据
     * @return 保存的记录ID，失败返回null
     */
    private Long saveGroupAnalysisRecord(List<Long> profileIds, Map<String, Object> resultData) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                log.warn("保存群体分析记录失败：未获取到当前用户");
                return null;
            }

            AnalysisRecord record = new AnalysisRecord();
            record.setAnalysisType("group");
            record.setUserId(currentUser.getId().longValue());
            record.setProfileIds(new ObjectMapper().writeValueAsString(profileIds));
            record.setResultData(new ObjectMapper().writeValueAsString(resultData));

            analysisRecordMapper.insert(record);
            log.info("群体分析记录保存成功，userId={}, recordId={}", currentUser.getId(), record.getId());
            return record.getId();
        } catch (Exception e) {
            log.warn("保存群体分析记录失败", e);
            return null;
        }
    }
}
