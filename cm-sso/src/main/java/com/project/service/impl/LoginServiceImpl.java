package com.project.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.DTO.PhoneLoginDTO;
import com.project.DTO.PhoneRegisterDTO;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.LoginMapper;
import com.project.service.LoginService;
import com.project.util.MD5Util;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper, User>
    implements LoginService {
    @Resource
    private LoginMapper loginMapper;

    /**
     * 手机登录
     * @param phoneLoginDTO 手机号、密码
     * @return token
     */
    @Override
    public String phoneLogin(PhoneLoginDTO phoneLoginDTO) {
        // 1.验证手机号和密码是否为空
        String phone = phoneLoginDTO.getPhone();
        String password = phoneLoginDTO.getPassword();

        if (StringUtils.isAnyBlank(phone, password)) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "手机号或密码为空"));
        }

        //todo 2.验证手机号和密码是否符合要求

        // 3.查看该登录用户是否已经注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_phone", phone);
        User user = loginMapper.selectOne(queryWrapper);

        // 4.判断查询的用户是否存在
        if (user == null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "该用户还未注册"));
        }

        // 5.存在，则验证密码是否正确
        String md5Password = MD5Util.calculateMD5(password);
        if (!Objects.equals(md5Password, user.getUserPassword())) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "密码错误"));
        }

        // 6.密码正确，生成token
        return TokenUtil.createToken(user.getUserId(), user.getUserPhone());
    }

    /**
     * 手机注册
     * @param phoneRegisterDTO 手机号码、密码、二次密码
     * @return string
     */
    @Override
    public boolean phoneRegister(PhoneRegisterDTO phoneRegisterDTO) {
        // 1.验证手机号、密码和二次密码是否为空
        String phone = phoneRegisterDTO.getPhone();
        String password = phoneRegisterDTO.getPassword();
        String checkPassword = phoneRegisterDTO.getConfirmPassword();

        if (StringUtils.isAnyBlank(phone, password, checkPassword)) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "手机号、密码或二次密码为空"));
        }

        //todo 2.验证手机号和密码是否符合要求

        // 3.校验该用户是否已经注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_phone", phone);
        User user = loginMapper.selectOne(queryWrapper);

        // 4.判断该用户是否存在
        if (user != null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "该用户已经注册"));
        }

        // 5.两次密码是否设置相同
        if (!Objects.equals(password, checkPassword)) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "两次密码不一致"));
        }

        // 6.密码加密
        String md5Password = MD5Util.calculateMD5(password);

        // 7.没有注册，存入用户信息
        User tempUser = new User();
        tempUser.setUserPhone(phone);
        tempUser.setUserPassword(md5Password);
        loginMapper.insert(tempUser);
        return true;
    }
}




