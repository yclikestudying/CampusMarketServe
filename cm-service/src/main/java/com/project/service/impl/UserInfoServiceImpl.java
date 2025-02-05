package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.VO.UserInfoVO;
import com.project.VO.UserVO;
import com.project.domain.Article;
import com.project.domain.Follows;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.ArticleMapper;
import com.project.mapper.FollowsMapper;
import com.project.mapper.UserInfoMapper;
import com.project.service.ArticleService;
import com.project.service.FollowsService;
import com.project.service.UserInfoService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import com.project.util.UploadAvatar;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, User>
        implements UserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private FollowsService followsService;

    @Resource
    private ArticleService articleService;

    /**
     * 获取用户个人信息
     *
     * @param token 解析出用户id和手机号
     * @return userInfo
     */
    @Override
    public UserInfoVO getUserInfo(String token) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");
        String phone = (String) stringObjectMap.get("phone");

        // 2. 根据用户id或用户手机号查询当前用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("user_phone", phone);

        // 数据脱敏
        User userInfo = userInfoMapper.selectOne(queryWrapper);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfo, userInfoVO);

        return userInfoVO;
    }

    /**
     * 修改用户个人信息
     *
     * @param token 解析出用户id和手机号
     * @param map   修改的数据 key：数据名 value：数据值
     * @return true or false
     */
    @Override
    public boolean updateUserInfo(String token, Map<String, Object> map) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 获取要修改的参数名和参数值
        String key = (String) map.get("key");
        if (StringUtils.isBlank(key)) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "修改数据为空"));
        }
        String value = null;
        Integer gender = null;
        if (!Objects.equals(key, "userGender")) {
            value = (String) map.get("value");
            if (StringUtils.isBlank(value)) {
                ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "修改数据为空"));
            }
        } else {
            gender = (Integer) map.get("value");
            if (gender == null) {
                ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "修改数据为空"));
            }
        }

        // 3. 选择要修改的数据
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        User user = new User();
        switch (key) {
            // 修改用户名
            case "userName":
                updateWrapper.set("user_name", value);
                user.setUserName(value);
                break;
            // 修改密码
            case "userPassword":
                // todo 旧密码匹对 新密码加密修改
                updateWrapper.set("user_password", value);
                user.setUserPassword(value);
                break;
            // 修改性别
            case "userGender":
                updateWrapper.set("user_gender", gender);
                user.setUserGender(gender);
                break;
            // 修改生日
            case "userBirthday":
                updateWrapper.set("user_birthday", value);
                user.setUserBirthday(value);
                break;
            // 修改简介
            case "userProfile":
                updateWrapper.set("user_profile", value);
                user.setUserProfile(value);
                break;
            // 修改所在地
            case "userLocation":
                updateWrapper.set("user_location", value);
                user.setUserLocation(value);
                break;
            // 修改家乡
            case "userHomeTown":
                updateWrapper.set("user_hometown", value);
                user.setUserHometown(value);
                break;
            // 修改专业
            case "userProfession":
                updateWrapper.set("user_profession", value);
                user.setUserProfession(value);
                break;
            // 修改标签
            case "userTags":
                updateWrapper.set("user_tags", value);
                user.setUserTags(value);
                break;
            default:
                ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数名错误"));
        }

        // 4. 修改数据库数据
        return userInfoMapper.update(user, updateWrapper) == 1;
    }

    /**
     * 修改用户头像
     *
     * @param token 解析出用户id和手机号
     * @param file  照片文件
     * @return success or fail
     */
    @Override
    public String updateAvatar(String token, MultipartFile file) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 上传照片到阿里云服务器，并返回新的访问地址
        String newLink = null;
        try {
            newLink = UploadAvatar.uploadAvatar(file, "avatar");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 3. 存储照片地址
        User user = new User();
        user.setUserId(userId);
        user.setUserAvatar(newLink);
        userInfoMapper.updateById(user);

        return newLink;
    }

    /**
     * 模糊查询用户
     *
     * @param token    解析出用户id和手机号
     * @param username 用户名
     * @return 用户集合
     */
    @Override
    public List<UserVO> queryUser(String token, String username) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 校验 username
        if (Objects.equals(username, "")) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("user_name", username)
                .ne("user_id", userId);
        List<User> users = userInfoMapper.selectList(queryWrapper);

        // 4. 进行数据脱敏
        List<UserVO> list = new ArrayList<>();
        users.forEach(user -> {
            UserVO userVO = new UserVO();
            userVO.setUserId(user.getUserId());
            userVO.setUserName(user.getUserName());
            userVO.setUserAvatar(user.getUserAvatar());
            list.add(userVO);
        });
        return list;
    }

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public UserVO getUserInfoByUserId(Long userId) {
        // 1. 校验用户id
        if (userId == null || userId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 2. 构造查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User user = userInfoMapper.selectOne(queryWrapper);

        // 3. 数据脱敏
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 查询动态、互关、关注和粉丝的数量
     *
     * @param token
     * @return
     */
    @Override
    public Map<String, Integer> getOtherInfo(String token) {
        Map<String, Integer> map = new HashMap<>();
        // 1. 查询我的动态数量
        List<Article> articles = articleService.queryAll(token);
        map.put("动态", articles != null ? articles.size() : 0);

        // 2. 查询我的关注数量
        List<User> users = followsService.followerUser(token);
        map.put("关注", users != null ? users.size() : 0);

        // 3. 查询我的粉丝数量
        List<User> users1 = followsService.followeeUser(token);
        map.put("粉丝", users1 != null ? users1.size() : 0);

        // 4. 查询互关数量
        List<User> users2 = followsService.eachFollow(token);
        map.put("互关", users2 != null ? users2.size() : 0);

        return map;
    }
}
