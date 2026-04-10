package com.cucn.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/diabetes")
@Slf4j  // 替换System.out为日志框架
public class DiabetesController {

    @Value("${files.pythonExe.path}")
    private String pythonPath;

    @Value("${files.pythonChatScript.path}")
    private String pythonScriptPath;

    @Value("${zhipu.api.key}")
    private String apiKey;

    // 超时时间设置(5分钟)
    private static final long PROCESS_TIMEOUT = 300;

    @PostMapping("/chat")
    public Result<String> chat(@RequestParam String question) {
        // 日志记录收到的问题
        log.info("[Java后端] 收到问题:{}", question);

        // 参数校验
        if (question == null || question.trim().isEmpty()) {
            return Result.error("问题不能为空");
        }

        Process process = null;
        try {
            // 构造调用Python的命令
            String[] command = {
                    pythonPath,
                    pythonScriptPath,
                    apiKey,
                    question
            };

            // 启动进程
            process = new ProcessBuilder(command)
                    .redirectErrorStream(false)  // 错误流单独处理
                    .start();

            // 异步读取输出流(防止阻塞)
            StringBuilder answerBuilder = new StringBuilder();
            Process finalProcess = process;
            Thread outputThread = new Thread(() -> readStream(finalProcess.getInputStream(), answerBuilder, false));
            outputThread.start();

            // 异步读取错误流
            StringBuilder errorMsg = new StringBuilder();
            Process finalProcess1 = process;
            Thread errorThread = new Thread(() -> readStream(finalProcess1.getErrorStream(), errorMsg, true));
            errorThread.start();

            // 等待进程完成,设置超时
            boolean finished = process.waitFor(PROCESS_TIMEOUT, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();  // 超时强制销毁进程
                return Result.error("Python执行超时,请稍后重试");
            }

            // 等待流读取线程结束
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
            // 确保进程资源释放
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    /**
     * 统一读取输入流的方法
     */
    private void readStream(InputStream inputStream, StringBuilder builder, boolean isError) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
                if (isError) {
                    log.error("[Python错误] {}", line);
                } else if (line.startsWith("[Python服务]")) {
                    log.info("[Python调试] {}", line);
                }
            }
        } catch (IOException e) {
            log.error("读取流失败", e);
        }
    }

    // 统一响应格式
    public static class Result<T> {
        private int code;
        private String msg;
        private T data;

        public static <T> Result<T> success(T data) {
            Result<T> result = new Result<>();
            result.code = 200;
            result.msg = "success";
            result.data = data;
            return result;
        }

        public static <T> Result<T> error(String msg) {
            Result<T> result = new Result<>();
            result.code = 500;
            result.msg = msg;
            return result;
        }

        // Getter和Setter
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMsg() { return msg; }
        public void setMsg(String msg) { this.msg = msg; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}