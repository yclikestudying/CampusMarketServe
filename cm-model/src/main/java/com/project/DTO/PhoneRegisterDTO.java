package com.project.DTO;


import lombok.Data;

@Data
public class PhoneRegisterDTO extends Phone {
    /**
     * 验证密码
     */
    private String confirmPassword;
}
