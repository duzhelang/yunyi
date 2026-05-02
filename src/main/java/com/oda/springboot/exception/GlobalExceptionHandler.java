package com.oda.springboot.exception;

import com.oda.springboot.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handle(ServiceException se){
        return Result.error(se.getCode(), se.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public Result handle(IllegalStateException e){
        return Result.error("500", e.getMessage());
    }

}
