package com.cucn.springboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一返回包装?
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {  // 增加泛型支持,提高类型安全?

    private String code;
    private String msg;
    private T data;  // 使用泛型类型

    /**
     * 成功返回(无数据?
     */
    public static Result<Void> success() {
        return new Result<>(Constants.CODE_200, "操作成功", null);
    }

    /**
     * 成功返回(带数据?
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(Constants.CODE_200, "操作成功", data);
    }

    /**
     * 成功返回(自定义消息+数据?
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(Constants.CODE_200, msg, data);
    }

    /**
     * 错误返回(自定义状态码+消息?
     */
    public static <T> Result<T> error(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 错误返回(默认系统错误)
     */
    public static <T> Result<T> error() {
        return new Result<>(Constants.CODE_500, "系统错误", null);
    }

    /**
     * 错误返回(自定义消息,使用默认错误码?
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(Constants.CODE_500, msg, null);
    }
}