package com.project.controller;

import com.project.DTO.PhoneLoginDTO;
import com.project.DTO.PhoneRegisterDTO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "用户登录注册")
public class LoginController {
    @Resource
    private LoginService loginService;

    /**
     * 用户手机登录
     * @return token
     */
    @PostMapping("/phoneLogin")
    @ApiOperation(value = "用户登录")
    public Result<Map<String, Object>> login(@RequestBody PhoneLoginDTO phoneLoginDTO) {
        Map<String, Object> map = loginService.phoneLogin(phoneLoginDTO);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 用户手机注册
     * @return message
     */
    @PostMapping("/phoneRegister")
    @ApiOperation(value = "用户注册")
    public Result<String> register(@RequestBody PhoneRegisterDTO phoneRegisterDTO) {
        boolean result = loginService.phoneRegister(phoneRegisterDTO);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.success(ResultCodeEnum.FAIL);
    }
}
