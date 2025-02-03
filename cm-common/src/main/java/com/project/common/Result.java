package com.project.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result<T> {
    // 响应码
    private Integer code;
    // 响应信息
    private String message;
    // 响应数据
    private T data;
    // 响应时间戳
    private Long timestamp;

    // 请求成功或失败的共同部分
    public static <T> Result<T> build(Integer code, String message, T data) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // 请求成功携带数据
    public static <T> Result<T> success(ResultCodeEnum resultCodeEnum, T data) {
        return build(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), data);
    }
    // 请求成功不携带数据
    public static <T> Result<T> success(ResultCodeEnum resultCodeEnum) {
        return build(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }
    // 请求失败
    public static <T> Result<T> fail(Integer code, String message) {
        return build(code, message, null);
    }
    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum) {
        return build(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }
}
