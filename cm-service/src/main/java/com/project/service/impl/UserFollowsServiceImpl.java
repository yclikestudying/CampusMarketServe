package com.project.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.domain.UserFollows;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.UserFollowsMapper;
import com.project.service.UserFollowsService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserFollowsServiceImpl extends ServiceImpl<UserFollowsMapper, UserFollows> implements UserFollowsService {
    @Resource
    private UserFollowsMapper userFollowsMapper;

    private Long userId;

    /**
     * 关注用户
     *
     * @param token      用户id和手机号
     * @param followedId 被关注者id
     * @return true or false
     */
    @Override
    public boolean addUser(String token, Long followedId) {
        // 1. 判断是否已经关注
        UserFollows isExists = userFollowCommon(token, followedId);
        if (isExists != null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "用户已经关注"));
        }

        // 2. 存入数据
        UserFollows userFollows = new UserFollows();
        userFollows.setFollowerId(userId);
        userFollows.setFollowedId(followedId);
        return userFollowsMapper.insert(userFollows) == 1;
    }

    /**
     * 取消关注
     *
     * @param token      用户id和手机号
     * @param followedId 被关注者id
     * @return true or false
     */
    @Override
    public boolean deleteUser(String token, Long followedId) {
        // 1. 判断是否已经关注
        UserFollows isExists = userFollowCommon(token, followedId);
        if (isExists == null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "用户还未关注"));
        }

        // 2. 取消关注
        return userFollowsMapper.deleteById(isExists.getFollowId()) == 1;
    }

    /**
     * 查询当前一个用户是否被关注
     *
     * @param token      用户id和手机号
     * @param followedId 被关注者id
     * @return true or false
     */
    @Override
    public boolean queryUserFollow(String token, Long followedId) {
        // 1. 判断是否已经关注
        UserFollows isExists = userFollowCommon(token, followedId);
        return isExists != null;
    }

    /**
     * 查询互关、关注以及粉丝
     *
     * @param token 用户id和手机号
     * @return list集合
     */
    @Override
    public List<List<Long>> queryUserFollowData(String token) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");
        List<List<Long>> list = new ArrayList<>();

        // 2. 查询出我关注的用户
        QueryWrapper<UserFollows> followerQueryWrapper = new QueryWrapper<>();
        followerQueryWrapper.eq("follower_id", userId);
        List<UserFollows> followerList = userFollowsMapper.selectList(followerQueryWrapper);
        List<Long> followerIdList = new ArrayList<>();
        followerList.forEach(follower -> {
            followerIdList.add(follower.getFollowedId());
        });
        list.add(followerIdList);
        List<Long> followCommon = new ArrayList<>(followerIdList);

        // 3. 查询出关注我的用户
        QueryWrapper<UserFollows> followedQueryWrapper = new QueryWrapper<>();
        followedQueryWrapper.eq("followed_id", userId);
        List<UserFollows> followedList = userFollowsMapper.selectList(followedQueryWrapper);
        List<Long> followedIdList = new ArrayList<>();
        followedList.forEach(follow -> {
            followedIdList.add(follow.getFollowerId());
        });
        list.add(followedIdList);

        // 4. 查询出既关注我的、我又关注的用户
        if (!followerIdList.isEmpty() && !followedIdList.isEmpty()) {
            followCommon.retainAll(followedIdList);
            list.add(followCommon);
        }
        return list;
    }

    /**
     * 关注用户、取消关注用户、查询当前一个用户是否被关注的共同部分
     */
    private UserFollows userFollowCommon(String token, Long followedId) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        userId = (Long) stringObjectMap.get("userId");

        // 2. 校验 followedId
        if (followedId == null || followedId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 检查是否已经关注
        QueryWrapper<UserFollows> userFollowsQueryWrapper = new QueryWrapper<>();
        userFollowsQueryWrapper.eq("follower_id", userId).eq("followed_id", followedId);
        return userFollowsMapper.selectOne(userFollowsQueryWrapper);
    }
}




