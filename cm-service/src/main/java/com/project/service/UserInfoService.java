package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.UserInfoVO;
import com.project.VO.UserVO;
import com.project.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserInfoService extends IService<User> {
    // 获取用户个人信息
    UserInfoVO getUserInfo(String token);

    // 修改个人信息
    boolean updateUserInfo(String token, Map<String, Object> map);

    // 修改用户头像
    String updateAvatar(String token, MultipartFile file);

    // 指定用户查询
    List<UserVO> queryUser(String token, String username);

    // 根据用户id查询用户信息
    User getUserInfoByUserId(Long userId);

    // 查询动态、互关、关注和粉丝的数量
    Map<String, Integer> getOtherInfo(String token);
}
