package com.project.exception;

import com.project.common.ResultCodeEnum;
import lombok.Getter;

@Getter
public class BusinessExceptionHandler extends RuntimeException{
    /**
     * 错误码
     */
    private final int code;

    /**
     * 异常构造器
     * @param message 消息
     * @param code 响应码
     */
    public BusinessExceptionHandler(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 异常构造器
     * @param code 响应码
     */
    public BusinessExceptionHandler(int code) {
        this(code, "");
    }

    /**
     * @param resultCodeEnum 消息和响应码
     */
    public BusinessExceptionHandler(ResultCodeEnum resultCodeEnum) {
        this(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }

}
