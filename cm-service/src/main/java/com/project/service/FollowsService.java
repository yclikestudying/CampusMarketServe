package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.domain.Follows;
import com.project.domain.User;

import java.util.List;

public interface FollowsService extends IService<Follows> {
    // 关注其他用户
    boolean follow(String token, Long otherUserId);

    // 取消关注
    boolean cancelFollow(String token, Long otherUserId);

    // 查询我的关注
    List<User> followerUser(String token, Long otherUserId);

    // 查询我的粉丝
    List<User> followeeUser(String token, Long otherUserId);

    // 相互关注
    List<User> eachFollow(String token, Long otherUserId);

    // 查询是否关注
    boolean isFollow(String token, Long otherUserId);
}
