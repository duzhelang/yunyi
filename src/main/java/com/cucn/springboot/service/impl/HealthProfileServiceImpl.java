package com.cucn.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cucn.springboot.entity.HealthProfile;
import com.cucn.springboot.mapper.HealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthProfileServiceImpl {

    @Autowired
    private HealthProfileMapper healthProfileMapper;

    // ⚠️ 配置路径:请确保 D 盘有这个文件夹,或者代码会自动创建
    // 建议:如果是 Linux/Mac,请改为 "/tmp/health-data" 或其他有效路?
    private static final String BASE_DIR = "D:\\Software-Cup121\\data";
    private static final String CSV_DIR = BASE_DIR + "\\csv_for_doctor";
    private static final String UPLOAD_DIR = BASE_DIR + "\\uploads";

    /**
     * 保存档案到数据库
     */
    public Long saveProfile(Integer pregnancies, Double glucose, Integer bloodPressure,
                            Integer skinThickness, Double insulin, Double bmi,
                            Double diabetesPedigreeFunction, Integer age,
                            String symptoms, MultipartFile file) throws IOException {

        HealthProfile profile = new HealthProfile();
        // 设置大驼峰字?
        profile.setPregnancies(pregnancies);
        profile.setGlucose(glucose);
        profile.setBloodPressure(bloodPressure);
        profile.setSkinThickness(skinThickness);
        profile.setInsulin(insulin);
        profile.setBMI(bmi);
        profile.setDiabetesPedigreeFunction(diabetesPedigreeFunction);
        profile.setAge(age);

        profile.setSymptoms(symptoms);
        profile.setUserId(1L); // TODO: 替换为当前登录用?ID
        profile.setStatus("PENDING"); // ?初始状态:待诊?
        profile.setCreateTime(LocalDateTime.now());

        // 处理文件上传
        if (file != null && !file.isEmpty()) {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            // 防止中文文件名问题,简单处?
            fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

            File dest = new File(UPLOAD_DIR + "\\" + fileName);
            file.transferTo(dest);
            profile.setFileUrl(fileName);
        }

        healthProfileMapper.insert(profile);
        return profile.getId();
    }

    /**
     * 生成 CSV 文件并更新数据库状态
     */
    public String generateCsvForDiagnostician(Long profileId) throws Exception {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) throw new RuntimeException("档案不存在");

        // 确保目录存在
        File dir = new File(CSV_DIR);
        if (!dir.exists()) dir.mkdirs();

        // 生成文件名
        String csvFileName = "patient_" + profileId + "_" + System.currentTimeMillis() + ".csv";
        String csvFullPath = CSV_DIR + "\\" + csvFileName;

        // 写入 CSV 内容
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(csvFullPath), StandardCharsets.UTF_8))) {
            // 表头 (方便医生用Excel 查看)
            writer.println("ID,Pregnancies,Glucose,BloodPressure,SkinThickness,Insulin,BMI,DiabetesPedigreeFunction,Age,Symptoms,CreateTime");

            // 数据行
            writer.printf("%d,%d,%f,%d,%d,%f,%f,%f,%d,\"%s\",%s%n",
                    profile.getId(),
                    profile.getPregnancies() != null ? profile.getPregnancies() : 0,
                    profile.getGlucose() != null ? profile.getGlucose() : 0.0,
                    profile.getBloodPressure() != null ? profile.getBloodPressure() : 0,
                    profile.getSkinThickness() != null ? profile.getSkinThickness() : 0,
                    profile.getInsulin() != null ? profile.getInsulin() : 0.0,
                    profile.getBMI() != null ? profile.getBMI() : 0.0,
                    profile.getDiabetesPedigreeFunction() != null ? profile.getDiabetesPedigreeFunction() : 0.0,
                    profile.getAge() != null ? profile.getAge() : 0,
                    (profile.getSymptoms() != null ? profile.getSymptoms().replace("\"", "\"\"") : ""), // 转义双引号
                    profile.getCreateTime()
            );
        }

        // 更新数据库
        profile.setCsvFilePath(csvFullPath);
        profile.setStatus("PENDING"); // 确保状态是待诊断
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
        wrapper.eq("user_id", userId).orderByDesc("create_time");
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
}