package com.oda.springboot.exception;

import com.oda.springboot.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handle(ServiceException se){
        return Result.error(se.getCode(), se.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public Result handle(IllegalStateException e){
        log.warn("非法状态异常: {}", e.getMessage());
        return Result.error("500", e.getMessage());
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseBody
    public Result handle(TimeoutException e){
        log.warn("任务超时: {}", e.getMessage());
        return Result.error("500", "任务处理超时，请稍后重试");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result handle(IllegalArgumentException e){
        log.warn("参数异常: {}", e.getMessage());
        return Result.error("400", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handle(Exception e){
        log.error("系统异常", e);
        return Result.error("500", "服务器内部错误，请稍后重试");
    }
}
