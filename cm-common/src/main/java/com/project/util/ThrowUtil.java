package com.project.util;


import com.project.common.ResultCodeEnum;
import com.project.exception.BusinessExceptionHandler;

/**
 * 异常统一封装工具
 */
public class ThrowUtil {
    public static void throwByObject(BusinessExceptionHandler businessExceptionHandler) {
        throw businessExceptionHandler;
    }

    public static void throwByCodeAndMessage(ResultCodeEnum resultCodeEnum) {
        throwByObject(new BusinessExceptionHandler(resultCodeEnum.getCode(), resultCodeEnum.getMessage()));
    }
}
