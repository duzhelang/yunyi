package com.cucn.springboot.controller;

import com.cucn.springboot.common.Result;
import com.cucn.springboot.entity.HealthProfile;
import com.cucn.springboot.service.impl.HealthProfileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health-profile")
@CrossOrigin(origins = "*") // 允许前端跨域访问
public class HealthProfileController {

    @Autowired
    private HealthProfileServiceImpl healthProfileService;

    // 从配置文件读取CSV根路径(适配Windows系统?
    @Value("${file.csv.root-path:D:\\Software-Cup121\\data\\csv_for_doctor}")
    private String csvRootPath;

    /**
     * 1. 保存健康档案 (用户?
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
            Long savedId = healthProfileService.saveProfile(
                    Pregnancies, Glucose, BloodPressure, SkinThickness,
                    Insulin, BMI, DiabetesPedigreeFunction, Age, symptoms, file
            );
            return Result.success(String.valueOf(savedId), "档案保存成功,请点击发送诊断");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败:" + e.getMessage());
        }
    }

    /**
     * 2. 生成 CSV 并标记为"待诊断?(用户点击发?
     */
    @PostMapping("/send-to-doctor/{id}")
    public Result<String> sendToDiagnostician(@PathVariable Long id) {
        try {
            String fileName = healthProfileService.generateCsvForDiagnostician(id);

            String msg = "已生成诊断档案CSV:<b>" + fileName + "</b><br>" +
                    "文件已保存至服务器,等待诊断员下载分析<br>" +
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
        System.out.println("🔍 接收到下载请求,ID: " + id);
        System.out.println("📌 CSV根路径配置:" + csvRootPath);
        System.out.println("=====================");

        FileInputStream fis = null;
        OutputStream os = null;

        try {
            // 1. 从数据库获取记录
            HealthProfile profile = healthProfileService.getById(id);
            if (profile == null) {
                System.err.println("下载失败:记录不存在 (ID: " + id + ")");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "记录不存在(ID: " + id + ")");
                return;
            }

            // 2. 拼接完整路径(兼容所有系统)
            // 方式1:如果数据库存的是完整路?
            String filePath = profile.getCsvFilePath();
            File file = null;

            if (filePath != null && !filePath.trim().isEmpty()) {
                // 处理路径分隔符,适配Windows
                filePath = filePath.replace("/", File.separator);
                file = new File(filePath);
            } else {
                // 方式2:如果数据库只存文件名,拼接根路?
                String fileName = profile.getCsvFileName(); // 需确保实体类有该字?
                if (fileName == null || fileName.trim().isEmpty()) {
                    System.err.println("下载失败:ID=" + id + " 未生成CSV文件(无文件名)");
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "该档案尚未生成CSV 文件,请先点击发送诊断!");
                    return;
                }
                // 使用根路?+ 文件名拼接完整路?
                file = new File(csvRootPath, fileName);
            }

            // 3. 检查文件是否真实存在
            if (file == null || !file.exists()) {
                String errorPath = file == null ? "未知路径" : file.getAbsolutePath();
                System.err.println("下载失败:文件不存在 " + errorPath);
                // 打印目录下所有文件,方便调试
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

            // 设置响应?(必须?getOutputStream 之前)
            response.setContentType("text/csv; charset=UTF-8");
            // 兼容旧版Excel:application/vnd.ms-excel
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // 流式?CSV
            os = response.getOutputStream();
            // 写入 UTF-8 BOM (解决Excel中文乱码问题)
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            // 读取文件并写入响应流
            fis = new FileInputStream(file);
            byte[] buffer = new byte[4096]; // 4KB缓冲区,提升读写效率
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush(); // 强制刷出缓冲区,确保文件完整下载

            System.out.println("文件发送成功 " + file.getName());

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
            return Result.success("诊断结果已提交,用户端可见!");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交失败:" + e.getMessage());
        }
    }

    /**
     * 5. 获取历史记录 (用户?
     */
    @GetMapping("/list")
    public Result<List<HealthProfile>> list() {
        // TODO: 实际应从 Token 解析 userId
        Long currentUserId = 1L;
        List<HealthProfile> list = healthProfileService.getListByUserId(currentUserId);
        return Result.success(list);
    }

    /**
     * 6. 获取待诊断列?(医生?
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
}