package com.oda.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oda.springboot.common.Result;
import com.oda.springboot.controller.dto.SaveAndPredictRequest;
import com.oda.springboot.entity.HealthProfile;
import com.oda.springboot.entity.User;
import com.oda.springboot.mapper.HealthProfileMapper;
import com.oda.springboot.service.ISinglePredictService;
import com.oda.springboot.service.impl.HealthProfileServiceImpl;
import com.oda.springboot.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/health-profile")
@CrossOrigin(origins = "*")
public class HealthProfileController {

    private static final Logger log = LoggerFactory.getLogger(HealthProfileController.class);

    @Autowired
    private HealthProfileServiceImpl healthProfileService;

    @Autowired
    private HealthProfileMapper healthProfileMapper;

    @Autowired
    private ISinglePredictService singlePredictService;

    private static final ConcurrentHashMap<String, String> ADVICE_CACHE = new ConcurrentHashMap<>();

    @Value("${files.pythonExe.path}")
    private String pythonPath;

    @Value("${files.pythonChatScript.path}")
    private String pythonChatScriptPath;

    @Value("${zhipu.api.key:}")
    private String zhipuApiKey;

    @Value("${file.csv.root-path:#{systemProperties['user.dir'] + '/data/csv_for_doctor'}}")
    private String csvRootPath;

    /**
     * 1. 保存健康档案 (用户)
     */
    @PostMapping("/save")
    public Result save(
            @RequestParam Integer Pregnancies,
            @RequestParam Double Glucose,
            @RequestParam Integer BloodPressure,
            @RequestParam Integer SkinThickness,
            @RequestParam Double Insulin,
            @RequestParam Double BMI,
            @RequestParam Double DiabetesPedigreeFunction,
            @RequestParam Integer Age,
            @RequestParam(required = false) String symptoms,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            User currentUser = TokenUtils.getCurrentUser();
            Long userId = currentUser != null ? currentUser.getId().longValue() : null;
            Long savedId = healthProfileService.saveProfile(
                    Pregnancies, Glucose, BloodPressure, SkinThickness,
                    Insulin, BMI, DiabetesPedigreeFunction, Age, symptoms, file, userId
            );
            return Result.success(String.valueOf(savedId), "档案保存成功,请点击发送诊断");
        } catch (Exception e) {
            log.error("保存健康档案失败", e);
            return Result.error("保存失败:" + e.getMessage());
        }
    }

    /**
     * 2. 生成 CSV 并标记为"待诊断档案"(用户点击发送)
     */
    @PostMapping("/send-to-doctor/{id}")
    public Result<String> sendToDiagnostician(@PathVariable Long id) {
        try {
            String fileName = healthProfileService.generateCsvForDiagnostician(id);

            String msg = "已生成诊断档案CSV:<b>" + fileName + "</b><br>" +
                    "文件已保存至服务器，等待诊断员下载分析<br>" +
                    "当前状态:<span style='color:orange'>等待诊断</span>";

            return Result.success(fileName, msg);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("生成失败:" + e.getMessage());
        }
    }

    /**
     * 3. 医生下载 CSV 文件(适配Windows路径,通用跨系统版本)
     */
    @GetMapping("/download-csv/{id}")
    public void downloadCsv(@PathVariable Long id, HttpServletResponse response) {
        // 强制打印请求日志(核心调试)
        System.out.println("=====================");
        System.out.println("🔍 接收到下载请求，ID: " + id);
        System.out.println("📌 CSV根路径配置: " + csvRootPath);
        System.out.println("=====================");

        FileInputStream fis = null;
        OutputStream os = null;

        try {
            // 1. 从数据库获取记录
            HealthProfile profile = healthProfileService.getById(id);
            if (profile == null) {
                System.err.println("下载失败:记录不存在(ID: " + id + ")");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "记录不存在(ID: " + id + ")");
                return;
            }

            // 2. 拼接完整路径(兼容所有系统)
            // 方式1:如果数据库存的是完整路径
            String filePath = profile.getCsvFilePath();
            File file = null;

            if (filePath != null && !filePath.trim().isEmpty()) {
                // 处理路径分隔符，适配Windows
                filePath = filePath.replace("/", File.separator);
                file = new File(filePath);
            } else {
                // 方式2:如果数据库只存文件名,拼接根路径
                String fileName = profile.getCsvFileName(); // 需确保实体类有该字段
                if (fileName == null || fileName.trim().isEmpty()) {
                    System.err.println("下载失败:ID=" + id + " 未生成CSV文件(无文件名)");
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "该档案尚未生成CSV 文件,请先点击发送诊断");
                    return;
                }
                // 使用根路径 + 文件名拼接完整路径
                file = new File(csvRootPath, fileName);
            }

            // 3. 检查文件是否真实存在
            if (file == null || !file.exists()) {
                String errorPath = file == null ? "未知路径" : file.getAbsolutePath();
                System.err.println("下载失败:文件不存在:" + errorPath);
                // 打印目录下所有文件，方便调试
                File rootDir = new File(csvRootPath);
                if (rootDir.exists() && rootDir.isDirectory()) {
                    String[] files = rootDir.list();
                    System.err.println("📂 根目录下的文件列表:" + (files == null ? "空" : String.join(", ", files)));
                } else {
                    System.err.println("📂 根目录不存在或不是目录:" + csvRootPath);
                }
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "服务器文件丢失:" + errorPath);
                return;
            }

            System.out.println("准备下载文件: " + file.getAbsolutePath());

            // 设置响应头(必须在getOutputStream 之前)
            response.setContentType("text/csv; charset=UTF-8");
            // 兼容旧版Excel:application/vnd.ms-excel
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // 流式输出CSV
            os = response.getOutputStream();
            // 写入 UTF-8 BOM (解决Excel中文乱码问题)
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            // 读取文件并写入响应流
            fis = new FileInputStream(file);
            byte[] buffer = new byte[4096]; // 4KB缓冲区，提升读写效率
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush(); // 强制刷出缓冲区，确保文件完整下载

            System.out.println("文件发送成功:" + file.getName());

        } catch (IOException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                try {
                    response.reset();
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("下载失败: " + e.getMessage());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                try {
                    response.reset();
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("系统错误: " + e.getMessage());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // 关闭资源(兜底)
            if (fis != null) {
                try { fis.close(); } catch (IOException e) {}
            }
            if (os != null) {
                try { os.close(); } catch (IOException e) {}
            }
        }
    }

    /**
     * 4. 医生提交诊断结果 (回填)
     */
    @PostMapping("/doctor/submit-result")
    public Result submitResult(@RequestBody Map<String, Object> params) {
        try {
            Long profileId = null;
            Object idObj = params.get("profileId");
            if (idObj instanceof Integer) {
                profileId = ((Integer) idObj).longValue();
            } else if (idObj instanceof Long) {
                profileId = (Long) idObj;
            }

            String resultText = (String) params.get("result");
            String label = params.get("predictionLabel") != null ? (String) params.get("predictionLabel") : null;

            if (profileId == null || resultText == null || resultText.trim().isEmpty()) {
                return Result.error("参数错误:ID 或诊断结果不能为空");
            }

            healthProfileService.submitDiagnosisResult(profileId, resultText, label);
            return Result.success("诊断结果已提交，用户端可见");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交失败:" + e.getMessage());
        }
    }

    /**
     * 5. 获取历史记录 (用户)
     */
    @GetMapping("/list")
    public Result<List<HealthProfile>> list() {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "请先登录");
            }
            Long currentUserId = currentUser.getId().longValue();
            List<HealthProfile> list = healthProfileService.getListByUserId(currentUserId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询健康档案列表失败", e);
            return Result.error("查询失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * 获取所有患者列表（医生使用，阶段四完善医生-患者关联后可改为按医生过滤）
     */
    @GetMapping("/list/by-doctor")
    public Result<List<HealthProfile>> listByDoctor() {
        try {
            List<HealthProfile> list = healthProfileService.getAllProfiles();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询患者列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 6. 获取待诊断列表(医生)
     */
    @GetMapping("/doctor/pending")
    public Result<List<HealthProfile>> getPendingList() {
        List<HealthProfile> list = healthProfileService.getPendingList();
        return Result.success(list);
    }

    /**
     * 7. 根据 ID 获取详情
     */
    @GetMapping("/{id}")
    public Result<HealthProfile> getById(@PathVariable Long id) {
        HealthProfile profile = healthProfileService.getById(id);
        if (profile == null) {
            return Result.error("记录不存在");
        }
        return Result.success(profile);
    }

    /**
     * 8. 保存健康档案并自动进行风险预测
     */
    @PostMapping("/save-and-predict")
    public Result saveAndPredict(@RequestBody SaveAndPredictRequest request) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            Long userId = currentUser != null ? currentUser.getId().longValue() : null;

            Long profileId = healthProfileService.saveProfileFull(
                request.getPregnancies(), request.getGlucose(),
                request.getBloodPressure(), request.getSkinThickness(),
                request.getInsulin(), request.getBmi(),
                request.getDiabetesPedigreeFunction(), request.getAge(),
                request.getSymptoms(), null,
                request.getHeight(), request.getWeight(),
                request.getExerciseFrequency(), request.getDietHabit(),
                request.getSmoking(), request.getDrinking(),
                request.getGender(), userId
            );

            Map<String, Object> features = new HashMap<>();
            features.put("pregnancies", request.getPregnancies() != null ? request.getPregnancies() : 0);
            features.put("glucose", request.getGlucose() != null ? request.getGlucose() : 0.0);
            features.put("bloodPressure", request.getBloodPressure() != null ? request.getBloodPressure() : 0);
            features.put("skinThickness", request.getSkinThickness() != null ? request.getSkinThickness() : 0);
            features.put("insulin", request.getInsulin() != null ? request.getInsulin() : 0.0);
            features.put("bmi", request.getBmi() != null ? request.getBmi() : 0.0);
            features.put("diabetesPedigreeFunction", request.getDiabetesPedigreeFunction() != null ? request.getDiabetesPedigreeFunction() : 0.0);
            features.put("age", request.getAge() != null ? request.getAge() : 0);

            Result predictResult = singlePredictService.singlePredict(features);
            Map<String, Object> predictionData = new HashMap<>();
            if ("200".equals(predictResult.getCode()) && predictResult.getData() != null) {
                predictionData = (Map<String, Object>) predictResult.getData();
                String riskLevel = (String) predictionData.getOrDefault("risk_level", "low");
                Double probability = predictionData.get("probability") instanceof Number
                    ? ((Number) predictionData.get("probability")).doubleValue()
                    : 0.0;
                String predictionJson = new ObjectMapper().writeValueAsString(predictionData);
                healthProfileService.updatePrediction(profileId, riskLevel, probability, predictionJson);
            }

            if (request.getAskAI() != null && request.getAskAI()) {
                String advice = generateStaticAdvice(predictionData);
                healthProfileService.updateAiAdvice(profileId, advice);
                predictionData.put("ai_advice", advice);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("id", profileId);
            result.put("prediction", predictionData);
            return Result.success(result);
        } catch (Exception e) {
            log.error("保存并预测失败", e);
            return Result.error("500", "保存并预测失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            healthProfileService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除健康档案失败", e);
            return Result.error("500", "删除失败: " + e.getMessage());
        }
    }

    /**
     * AI智能建议（优先调用AI模型，失败时回退到静态规则）
     */
    private String generateStaticAdvice(Map<String, Object> predictionData) {
        String riskLevel = (String) predictionData.getOrDefault("risk_level", "low");
        Double probability = predictionData.get("probability") instanceof Number
            ? ((Number) predictionData.get("probability")).doubleValue()
            : 0.0;

        String cacheKey = riskLevel + "_" + String.format("%.1f", probability);
        String cached = ADVICE_CACHE.get(cacheKey);
        if (cached != null) return cached;

        String aiAdvice = callAiForAdvice(riskLevel, probability, predictionData);
        if (aiAdvice != null && !aiAdvice.isBlank()) {
            ADVICE_CACHE.put(cacheKey, aiAdvice);
            return aiAdvice;
        }

        StringBuilder advice = new StringBuilder();
        if ("high".equals(riskLevel)) {
            advice.append("您的糖尿病风险较高（概率").append(String.format("%.1f", probability)).append("%），");
            advice.append("建议尽快就医，进行专业检查和治疗。");
        } else if ("medium".equals(riskLevel)) {
            advice.append("您的糖尿病风险中等（概率").append(String.format("%.1f", probability)).append("%），");
            advice.append("建议控制饮食，增加运动，定期监测血糖。");
        } else {
            advice.append("您的糖尿病风险较低（概率").append(String.format("%.1f", probability)).append("%），");
            advice.append("请继续保持健康的生活方式，定期体检。");
        }
        advice.append("建议每周至少进行150分钟中等强度运动，保持均衡饮食，控制碳水化合物摄入。");

        ADVICE_CACHE.put(cacheKey, advice.toString());
        return advice.toString();
    }

    private String callAiForAdvice(String riskLevel, Double probability, Map<String, Object> predictionData) {
        if (zhipuApiKey == null || zhipuApiKey.isBlank()) {
            return null;
        }
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位糖尿病专科医生，请根据以下预测结果生成个性化健康建议（150字以内）：\n");
        prompt.append("风险等级：").append(riskLevel).append("，预测概率：").append(String.format("%.1f", probability)).append("%\n");
        Object glucose = predictionData.get("glucose");
        Object bmi = predictionData.get("bmi");
        Object age = predictionData.get("age");
        if (glucose != null) prompt.append("血糖：").append(glucose).append(" mg/dL\n");
        if (bmi != null) prompt.append("BMI：").append(bmi).append("\n");
        if (age != null) prompt.append("年龄：").append(age).append("岁\n");

        Process process = null;
        try {
            String[] command = {
                pythonPath,
                pythonChatScriptPath,
                "glm-4-flash",
                zhipuApiKey,
                prompt.toString()
            };
            process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

            StringBuilder output = new StringBuilder();
            InputStream processInput = process.getInputStream();
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(processInput, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                } catch (IOException ignored) {}
            });
            outputThread.start();

            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                outputThread.interrupt();
                return null;
            }
            outputThread.join(2000);

            String result = output.toString().trim();
            if (process.exitValue() == 0 && !result.isEmpty()) {
                log.info("AI建议生成成功，长度: {}", result.length());
                return result;
            }
        } catch (Exception e) {
            log.warn("AI建议生成失败，回退到静态建议: {}", e.getMessage());
            if (Thread.currentThread().isInterrupted()) {
                log.warn("AI调用被中断");
            }
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        return null;
    }

    /**
     * 7. 家庭预测历史查询
     */
    @GetMapping("/dpf-history")
    public Result getDpfHistory() {
        try {
            Long userId = TokenUtils.getCurrentUser().getId().longValue();
            QueryWrapper<HealthProfile> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId)
                .isNotNull("DiabetesPedigreeFunction")
                .orderByDesc("create_time")
                .last("LIMIT 20");
            List<HealthProfile> profiles = healthProfileMapper.selectList(wrapper);

            List<Map<String, Object>> history = new ArrayList<>();
            for (HealthProfile p : profiles) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", p.getId());
                item.put("dpf", p.getDiabetesPedigreeFunction());
                item.put("age", p.getAge());
                item.put("glucose", p.getGlucose());
                item.put("bmi", p.getBMI());
                item.put("riskLevel", p.getRiskLevel());
                item.put("probability", p.getRiskProbability());
                item.put("createTime", p.getCreateTime());
                history.add(item);
            }
            return Result.success(history);
        } catch (Exception e) {
            log.error("查询DPF历史失败", e);
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }
}