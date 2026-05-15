package com.oda.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.oda.springboot.common.Result;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
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

    @Value("${mimo-tts.api.api-key:}")
    private String mimoTtsApiKey;

    @Value("${mimo-tts.api.base-url:}")
    private String mimoTtsBaseUrl;

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

    @PostMapping("/tts")
    public Result<String> textToSpeech(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        log.info("[TTS] 收到语音合成请求，文本长度:{}", text != null ? text.length() : 0);

        if (text == null || text.trim().isEmpty()) {
            return Result.error("文本不能为空");
        }

        if (mimoTtsApiKey == null || mimoTtsApiKey.isBlank()) {
            return Result.error("TTS API Key 未配置");
        }

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", "mimo-v2.5-tts-voicedesign");

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.set("role", "user");
            userMessage.set("content", "请用温柔亲切的年轻女性声音朗读以下内容");
            messages.add(userMessage);

            JSONObject assistantMessage = new JSONObject();
            assistantMessage.set("role", "assistant");
            assistantMessage.set("content", text);
            messages.add(assistantMessage);

            requestBody.set("messages", messages);

            JSONObject audio = new JSONObject();
            audio.set("format", "wav");
            audio.set("optimize_text_preview", true);
            requestBody.set("audio", audio);

            String requestUrl = mimoTtsBaseUrl + "/chat/completions";
            log.info("[TTS] 请求URL: {}", requestUrl);
            log.info("[TTS] API Key前10位: {}", mimoTtsApiKey.substring(0, Math.min(10, mimoTtsApiKey.length())));
            log.info("[TTS] 请求体: {}", requestBody.toString());

            HttpResponse response = HttpRequest.post(requestUrl)
                    .header("Authorization", "Bearer " + mimoTtsApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(60000)
                    .execute();

            log.info("[TTS] 响应状态码: {}", response.getStatus());
            log.info("[TTS] 响应内容前500字符: {}", response.body().substring(0, Math.min(500, response.body().length())));

            if (response.getStatus() != 200) {
                log.error("[TTS] API调用失败，状态码:{}, 响应:{}", response.getStatus(), response.body());
                return Result.error("语音合成服务调用失败，状态码: " + response.getStatus());
            }

            JSONObject responseJson = JSONUtil.parseObj(response.body());
            
            // 检查是否有错误
            if (responseJson.containsKey("error")) {
                JSONObject error = responseJson.getJSONObject("error");
                String errorMsg = error.getStr("message", "未知错误");
                log.error("[TTS] API返回错误: {}", errorMsg);
                return Result.error("TTS API错误: " + errorMsg);
            }
            
            JSONArray choices = responseJson.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                log.error("[TTS] 返回结果为空，响应: {}", response.body());
                return Result.error("语音合成返回结果为空");
            }

            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            
            if (message == null) {
                log.error("[TTS] message为空，firstChoice: {}", firstChoice.toString());
                return Result.error("语音合成返回数据格式错误：缺少message");
            }
            
            JSONObject audioResult = message.getJSONObject("audio");

            if (audioResult == null) {
                log.error("[TTS] audio为空，message: {}", message.toString());
                return Result.error("语音合成返回数据格式错误：缺少audio");
            }

            String audioData = audioResult.getStr("data");
            if (audioData == null || audioData.isEmpty()) {
                log.error("[TTS] audio.data为空，audioResult: {}", audioResult.toString());
                return Result.error("语音合成返回的音频数据为空");
            }

            log.info("[TTS] 语音合成成功，音频数据长度:{}", audioData.length());
            return Result.success(audioData);

        } catch (Exception e) {
            log.error("[TTS] 语音合成异常", e);
            return Result.error("语音合成服务调用异常: " + e.getMessage());
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