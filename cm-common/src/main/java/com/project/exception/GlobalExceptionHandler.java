package com.project.exception;

import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 全局异常捕获
    @ExceptionHandler(RuntimeException.class)
    public Result<String> exception(RuntimeException e) {
        return Result.fail(ResultCodeEnum.SYSTEM_ERROR.getCode(), "系统错误");//系统错误
    }

    // 自定义异常捕获
    @ExceptionHandler(BusinessExceptionHandler.class)
    public Result<String> businessException(BusinessExceptionHandler e) {
        return Result.fail(e.getCode(), e.getMessage());
    }
}
