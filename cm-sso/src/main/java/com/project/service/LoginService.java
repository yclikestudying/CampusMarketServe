package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.DTO.PhoneLoginDTO;
import com.project.DTO.PhoneRegisterDTO;
import com.project.domain.User;

import java.util.Map;

public interface LoginService extends IService<User> {
    // 手机登录
    Map<String, Object> phoneLogin(PhoneLoginDTO phoneLoginDTO);

    // 手机注册
    boolean phoneRegister(PhoneRegisterDTO phoneRegisterDTO);
}
