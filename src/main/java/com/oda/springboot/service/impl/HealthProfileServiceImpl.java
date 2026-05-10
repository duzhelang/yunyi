package com.oda.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oda.springboot.entity.HealthProfile;
import com.oda.springboot.mapper.HealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthProfileServiceImpl {

    @Autowired
    private HealthProfileMapper healthProfileMapper;
    // 建议:如果是 Linux/Mac,请改为 "/tmp/health-data" 或其他有效路径
//    private static final String BASE_DIR = "./data";
    private static final String BASE_DIR = System.getProperty("user.dir") + "/data";
    private static final String CSV_DIR = BASE_DIR + "/csv_for_doctor";
    private static final String UPLOAD_DIR = BASE_DIR + "/uploads";
    /**
     * 保存档案到数据库
     */
    public Long saveProfile(Integer pregnancies, Double glucose, Integer bloodPressure,
                            Integer skinThickness, Double insulin, Double bmi,
                            Double diabetesPedigreeFunction, Integer age,
                            String symptoms, MultipartFile file, Long userId) throws IOException {

        HealthProfile profile = new HealthProfile();
        profile.setPregnancies(pregnancies);
        profile.setGlucose(glucose);
        profile.setBloodPressure(bloodPressure);
        profile.setSkinThickness(skinThickness);
        profile.setInsulin(insulin);
        profile.setBMI(bmi);
        profile.setDiabetesPedigreeFunction(diabetesPedigreeFunction);
        profile.setAge(age);
        profile.setSymptoms(symptoms);
        profile.setUserId(userId != null ? userId : 1L);
        profile.setStatus("PENDING");
        profile.setCreateTime(LocalDateTime.now());

        // 处理文件上传
        if (file != null && !file.isEmpty()) {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            // 防止中文文件名问题,简单处?
            fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

            File dest = new File(UPLOAD_DIR + File.separator + fileName);
            file.transferTo(dest);
            profile.setFileUrl(fileName);
        }

        healthProfileMapper.insert(profile);
        return profile.getId();
    }

    /**
     * 生成 CSV 文件并更新数据库状态（增强版，含预测结果）
     */
    public String generateCsvForDiagnostician(Long profileId) throws Exception {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) throw new RuntimeException("档案不存在");

        File dir = new File(CSV_DIR);
        if (!dir.exists()) dir.mkdirs();

        String csvFileName = "patient_" + profileId + "_" + System.currentTimeMillis() + ".csv";
        String csvFullPath = CSV_DIR + File.separator + csvFileName;

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(csvFullPath), StandardCharsets.UTF_8))) {
            writer.println("ID,Pregnancies,Glucose,BloodPressure,SkinThickness,Insulin,BMI,DiabetesPedigreeFunction,Age,Symptoms,CreateTime,RiskLevel,RiskProbability");
            writer.printf("%d,%d,%f,%d,%d,%f,%f,%f,%d,\"%s\",%s,%s,%f%n",
                    profile.getId(),
                    profile.getPregnancies() != null ? profile.getPregnancies() : 0,
                    profile.getGlucose() != null ? profile.getGlucose() : 0.0,
                    profile.getBloodPressure() != null ? profile.getBloodPressure() : 0,
                    profile.getSkinThickness() != null ? profile.getSkinThickness() : 0,
                    profile.getInsulin() != null ? profile.getInsulin() : 0.0,
                    profile.getBMI() != null ? profile.getBMI() : 0.0,
                    profile.getDiabetesPedigreeFunction() != null ? profile.getDiabetesPedigreeFunction() : 0.0,
                    profile.getAge() != null ? profile.getAge() : 0,
                    (profile.getSymptoms() != null ? profile.getSymptoms().replace("\"", "\"\"") : ""),
                    profile.getCreateTime(),
                    profile.getRiskLevel() != null ? profile.getRiskLevel() : "",
                    profile.getRiskProbability() != null ? profile.getRiskProbability() : 0.0
            );
        }

        profile.setCsvFilePath(csvFullPath);
        profile.setStatus("PENDING");
        healthProfileMapper.updateById(profile);
        return csvFileName;
    }

    /**
     * 诊断员提交结果(重载版,支持 label)
     */
    public void submitDiagnosisResult(Long profileId, String result, String label) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) throw new RuntimeException("档案不存在");

        profile.setDiagnosisResult(result);
        profile.setStatus("DONE"); // 状态改为完成
        profile.setDiagnoseTime(LocalDateTime.now());

        // 如果有需要,可以将label 存入某个字段,或者拼接到 result 中
        // 例如:profile.setPredictionLabel(label);

        healthProfileMapper.updateById(profile);
    }

    // 兼容旧调?
    public void submitDiagnosisResult(Long profileId, String result) {
        submitDiagnosisResult(profileId, result, null);
    }

    /**
     * 获取用户历史记录
     */
    public List<HealthProfile> getListByUserId(Long userId) {
        QueryWrapper<HealthProfile> wrapper = new QueryWrapper<>();
        wrapper.select(HealthProfile.class, i -> !i.getProperty().equals("predictionJson"));
        wrapper.eq("user_id", userId).orderByDesc("create_time").last("LIMIT 100");
        return healthProfileMapper.selectList(wrapper);
    }

    /**
     * 获取所有健康档案（医生使用）
     */
    public List<HealthProfile> getAllProfiles() {
        QueryWrapper<HealthProfile> wrapper = new QueryWrapper<>();
        wrapper.select(HealthProfile.class, i -> !i.getProperty().equals("predictionJson"));
        wrapper.orderByDesc("create_time").last("LIMIT 200");
        return healthProfileMapper.selectList(wrapper);
    }

    /**
     * 获取所有待诊断列表 (医生专用)
     */
    public List<HealthProfile> getPendingList() {
        QueryWrapper<HealthProfile> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "PENDING").orderByDesc("create_time");
        return healthProfileMapper.selectList(wrapper);
    }

    /**
     * 根据 ID 获取单个对象 (辅助 Controller)
     */
    public HealthProfile getById(Long id) {
        return healthProfileMapper.selectById(id);
    }

    /**
     * 更新预测结果到健康档案
     */
    public void updatePrediction(Long profileId, String riskLevel, Double riskProbability, String predictionJson) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) throw new RuntimeException("档案不存在");
        profile.setRiskLevel(riskLevel);
        profile.setRiskProbability(riskProbability);
        profile.setPredictionJson(predictionJson);
        healthProfileMapper.updateById(profile);
    }

    /**
     * 更新AI健康建议
     */
    public void updateAiAdvice(Long profileId, String advice) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) throw new RuntimeException("档案不存在");
        profile.setAiAdvice(advice);
        healthProfileMapper.updateById(profile);
    }

    /**
     * 根据ID删除健康档案
     */
    public void deleteById(Long id) {
        healthProfileMapper.deleteById(id);
    }

    /**
     * 保存完整健康档案（含生活方式字段）
     */
    public Long saveProfileFull(Integer pregnancies, Double glucose, Integer bloodPressure,
                                Integer skinThickness, Double insulin, Double bmi,
                                Double diabetesPedigreeFunction, Integer age,
                                String symptoms, MultipartFile file,
                                Double height, Double weight,
                                String exerciseFrequency, String dietHabit,
                                String smoking, String drinking, String gender, Long userId) throws IOException {

        HealthProfile profile = new HealthProfile();
        profile.setPregnancies(pregnancies);
        profile.setGlucose(glucose);
        profile.setBloodPressure(bloodPressure);
        profile.setSkinThickness(skinThickness);
        profile.setInsulin(insulin);
        profile.setBMI(bmi);
        profile.setDiabetesPedigreeFunction(diabetesPedigreeFunction);
        profile.setAge(age);
        profile.setSymptoms(symptoms);

        profile.setHeight(height);
        profile.setWeight(weight);
        profile.setExerciseFrequency(exerciseFrequency);
        profile.setDietHabit(dietHabit);
        profile.setSmoking(smoking);
        profile.setDrinking(drinking);
        profile.setGender(gender);

        profile.setUserId(userId != null ? userId : 1L);
        profile.setStatus("PENDING");
        profile.setCreateTime(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            File dest = new File(UPLOAD_DIR + File.separator + fileName);
            file.transferTo(dest);
            profile.setFileUrl(fileName);
        }

        healthProfileMapper.insert(profile);
        return profile.getId();
    }
}