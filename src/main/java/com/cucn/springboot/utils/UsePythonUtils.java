package com.cucn.springboot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class UsePythonUtils {

    // 调用Python脚本并返回输出内容
    public static String callPythonAndGetOutput(String[] arguments) throws IOException, InterruptedException {
        if (arguments == null || arguments.length == 0) {
            throw new IllegalArgumentException("调用参数不能为空");
        }

        System.out.println("\n===== 开始调用Python脚本 =====");
        System.out.println("调用时间:" + new java.util.Date());
        System.out.println("执行的命令参数:");
        for (int i = 0; i < arguments.length; i++) {
            System.out.println("  参数[" + i + "]:" + arguments[i]);
        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(arguments);
            System.out.println("Python进程已启动,PID:" + getProcessId(process));

            // 读取标准输出
            StringBuilder output = new StringBuilder();
            try (InputStream stdout = process.getInputStream();
                 BufferedReader stdoutReader = new BufferedReader(
                         new InputStreamReader(stdout, StandardCharsets.UTF_8))
            ) {
                System.out.println("\n===== Python脚本标准输出 =====");
                String line;
                while ((line = stdoutReader.readLine()) != null) {
                    output.append(line).append("\n");
                    System.out.println(line);
                }
            }

            // 读取错误输出
            try (InputStream stderr = process.getErrorStream();
                 BufferedReader stderrReader = new BufferedReader(
                         new InputStreamReader(stderr, StandardCharsets.UTF_8))
            ) {
                System.out.println("\n===== Python脚本错误输出 =====");
                String line;
                while ((line = stderrReader.readLine()) != null) {
                    System.err.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("\n===== Python脚本执行结束 =====");
            System.out.println("退出码:" + exitCode);

            if (exitCode != 0) {
                throw new RuntimeException("Python脚本执行失败,退出码:" + exitCode);
            }

            return output.toString().trim();

        } catch (IOException e) {
            System.err.println("调用Python失败:" + e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            System.err.println("调用被中断:" + e.getMessage());
            Thread.currentThread().interrupt();
            throw e;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    // 原调用方法保留(用于其他场景)
    public static int callPython(String[] arguments) throws IOException, InterruptedException {
        if (arguments == null || arguments.length == 0) {
            throw new IllegalArgumentException("调用参数不能为空");
        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(arguments);
            return process.waitFor();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static long getProcessId(Process process) {
        try {
            java.lang.reflect.Field field = process.getClass().getDeclaredField("pid");
            field.setAccessible(true);
            return field.getLong(process);
        } catch (Exception e) {
            return -1;
        }
    }
}