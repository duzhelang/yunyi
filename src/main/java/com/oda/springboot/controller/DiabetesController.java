package com.oda.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.oda.springboot.common.Result;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/diabetes")
public class DiabetesController {

    private static final Logger log = LoggerFactory.getLogger(DiabetesController.class);

    @Value("${files.pythonExe.path}")
    private String pythonPath;

    @Value("${files.pythonChatScript.path}")
    private String pythonScriptPath;

    @Value("${zhipu.api.key:}")
    private String zhipuApiKey;

    @Value("${deepseek.api.api-key:}")
    private String deepseekApiKey;

    @Value("${kimi.api.api-key:}")
    private String kimiApiKey;

    @Value("${mimo.api.api-key:}")
    private String mimoApiKey;

    @Value("${mimo-omni.api.api-key:}")
    private String mimoOmniApiKey;

    private static final long PROCESS_TIMEOUT = 300;

    private String getApiKey(String keyValue, String keyName) {
        if (keyValue == null || keyValue.isBlank()) {
            throw new IllegalStateException(
                    keyName + " API Key 未配置，请检查 application.yml 中的相关配置");
        }
        return keyValue;
    }

    @PostMapping("/chat")
    public Result<String> chat(@RequestParam String question,
                               @RequestParam(required = false, defaultValue = "glm-4-flash") String provider) {
        log.info("[Java后端] 收到问题:{}, 模型:{}", question, provider);

        if (question == null || question.trim().isEmpty()) {
            return Result.error("问题不能为空");
        }

        String apiKey;
        String normalizedProvider;
        switch (provider.toLowerCase()) {
            case "glm-4-flash":
                apiKey = getApiKey(zhipuApiKey, "智谱");
                normalizedProvider = "glm-4-flash";
                break;
            case "glm-4.7-flash":
                apiKey = getApiKey(zhipuApiKey, "智谱");
                normalizedProvider = "glm-4.7-flash";
                break;
            case "deepseek":
                apiKey = getApiKey(deepseekApiKey, "DeepSeek");
                normalizedProvider = "deepseek";
                break;
            case "kimi":
                apiKey = getApiKey(kimiApiKey, "Kimi");
                normalizedProvider = "kimi";
                break;
            case "mimo-v2.5-pro":
                apiKey = getApiKey(mimoApiKey, "MiMo");
                normalizedProvider = "mimo-v2.5-pro";
                break;
            case "mimo-v2-flash":
                apiKey = getApiKey(mimoApiKey, "MiMo");
                normalizedProvider = "mimo-v2-flash";
                break;
            case "mimo-v2-omni":
                apiKey = getApiKey(mimoOmniApiKey, "MiMo-OMNI");
                normalizedProvider = "mimo-v2-omni";
                break;
            default:
                apiKey = getApiKey(zhipuApiKey, "智谱");
                normalizedProvider = "glm-4-flash";
                break;
        }

        Process process = null;
        try {
            String[] command = {
                    pythonPath,
                    pythonScriptPath,
                    normalizedProvider,
                    apiKey,
                    question
            };

            process = new ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start();

            StringBuilder answerBuilder = new StringBuilder();
            Process finalProcess = process;
            Thread outputThread = new Thread(() -> readStream(finalProcess.getInputStream(), answerBuilder, false));
            outputThread.start();

            StringBuilder errorMsg = new StringBuilder();
            Thread errorThread = new Thread(() -> readStream(finalProcess.getErrorStream(), errorMsg, true));
            errorThread.start();

            boolean finished = process.waitFor(PROCESS_TIMEOUT, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return Result.error("Python执行超时,请稍后重试");
            }

            outputThread.join(1000);
            errorThread.join(1000);

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("[Python执行错误] 退出码:{},错误信息:{}", exitCode, errorMsg);
                return Result.error("服务暂时无法回答,请稍后重试");
            }

            String answer = answerBuilder.toString().trim();
            if (answer.isEmpty()) {
                return Result.error("未获取到有效回答,请重试");
            }

            log.info("[Java后端] 返回回答:{}", answer);
            return Result.success(answer);

        } catch (IOException e) {
            log.error("调用Python脚本失败", e);
            return Result.error("服务调用失败,请检查配置");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("处理进程被中断", e);
            return Result.error("请求处理被中断,请重试");
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private void readStream(InputStream inputStream, StringBuilder builder, boolean isError) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
                if (isError) {
                    log.error("[Python错误] {}", line);
                } else if (line.startsWith("[Python服务]")) {
                    log.debug("[Python调试] {}", line);
                }
            }
        } catch (IOException e) {
            log.error("读取流失败", e);
        }
    }
}