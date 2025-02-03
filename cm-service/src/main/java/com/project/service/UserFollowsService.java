package com.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.project.domain.UserFollows;

import java.util.List;

public interface UserFollowsService extends IService<UserFollows> {

    // 关注用户
    boolean addUser(String token, Long userId);

    // 取消关注
    boolean deleteUser(String token, Long userId);

    // 查询当前一个用户是否被关注
    boolean queryUserFollow(String token, Long userId);

    // 查询互关、关注以及粉丝
    List<List<Long>> queryUserFollowData(String token);

}
