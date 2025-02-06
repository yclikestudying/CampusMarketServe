package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.domain.Follows;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.FollowsMapper;
import com.project.mapper.UserInfoMapper;
import com.project.service.FollowsService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FollowsServiceImpl extends ServiceImpl<FollowsMapper, Follows>
        implements FollowsService {

    @Resource
    private FollowsMapper followsMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 关注其他用户
     *
     * @param token
     * @param otherUserId 被关注者id
     * @return
     */
    @Override
    public boolean follow(String token, Long otherUserId) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 校验 otherUserId
        if (otherUserId == null || otherUserId < 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "被关注者id错误"));
        }

        // 3. 查询是否已经关注
        QueryWrapper<Follows> followsQueryWrapper = new QueryWrapper<>();
        followsQueryWrapper.eq("follower_id", userId)
                .eq("followee_id", otherUserId);
        Follows one = followsMapper.selectOne(followsQueryWrapper);
        if (one != null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "用户已经被关注"));
        }

        // 4. 构造条件
        Follows follows = new Follows();
        follows.setFollowerId(userId);
        follows.setFolloweeId(otherUserId);
        return followsMapper.insert(follows) > 0;
    }

    /**
     * 取消关注
     *
     * @param token
     * @param otherUserId 被关注者id
     * @return
     */
    @Override
    public boolean cancelFollow(String token, Long otherUserId) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 校验 otherUserId
        if (otherUserId == null || otherUserId < 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "被关注者id错误"));
        }

        // 3. 查询是否已经关注
        QueryWrapper<Follows> followsQueryWrapper = new QueryWrapper<>();
        followsQueryWrapper.eq("follower_id", userId)
                .eq("followee_id", otherUserId);
        Follows one = followsMapper.selectOne(followsQueryWrapper);
        if (one == null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "用户没有被关注"));
        }

        // 4. 构造条件
        return followsMapper.delete(followsQueryWrapper) > 0;
    }

    /**
     * 查询我的关注
     *
     * @param token
     * @return
     */
    @Override
    public List<User> followerUser(String token) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 查询数量
        QueryWrapper<Follows> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", userId);
        List<Follows> follows = followsMapper.selectList(queryWrapper);

        // 3. 获取被关注的id
        List<Long> idList = new ArrayList<>();
        if (!follows.isEmpty()) {
            follows.forEach(follow -> {
                idList.add(follow.getFolloweeId());
            });

            // 4. 查询我关注的用户的信息
            return userInfoMapper.selectBatchIds(idList);
        }

        return null;
    }

    /**
     * 查询我的粉丝
     *
     * @param token
     * @return
     */
    @Override
    public List<User> followeeUser(String token) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 查询数量
        QueryWrapper<Follows> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("followee_id", userId);
        List<Follows> follows = followsMapper.selectList(queryWrapper);

        // 3. 获取被关注的id
        List<Long> idList = new ArrayList<>();
        if (!follows.isEmpty()) {
            follows.forEach(follow -> {
                idList.add(follow.getFolloweeId());
            });

            // 4. 查询我关注的用户的信息
            return userInfoMapper.selectBatchIds(idList);
        }

        return null;
    }

    /**
     * 相互关注
     *
     * @param token
     * @return
     */
    @Override
    public List<User> eachFollow(String token) {
        // 1. 我的关注
        List<User> users = followerUser(token);

        // 2. 我的粉丝
        List<User> users1 = followeeUser(token);

        if (users != null && !users.isEmpty() && users1 != null && !users1.isEmpty()) {
            users.retainAll(users1);
            return users;
        }
        return null;
    }

    /**
     * 查询是否关注
     *
     * @param token
     * @return
     */
    @Override
    public boolean isFollow(String token, Long otherUserId) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 构造条件
        QueryWrapper<Follows> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", userId)
                .eq("followee_id", otherUserId);
        return followsMapper.selectOne(queryWrapper) != null;
    }
}
