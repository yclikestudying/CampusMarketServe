package com.project.controller;

import com.project.VO.UserInfoVO;
import com.project.VO.UserVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.domain.User;
import com.project.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userInfo")
@Api(tags = "用户个人信息")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;

    /**
     * 获取用户个人信息
     *
     * @param token 解析出用户id和手机号
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    @ApiOperation(value = "获取用户个人信息")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("Authorization") String token) {
        UserInfoVO userInfo = userInfoService.getUserInfo(token);
        return Result.success(ResultCodeEnum.SUCCESS, userInfo);
    }


    /**
     * 修改用户个人信息
     *
     * @param token 解析出用户id和手机号
     * @param map   用户修改的信息数据
     * @return success or fail
     */
    @PutMapping("/updateUserInfo")
    @ApiOperation(value = "修改用户个人信息")
    public Result<String> updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> map) {
        boolean result = userInfoService.updateUserInfo(token, map);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 修改用户头像
     *
     * @param token 解析出用户id和手机号
     * @param file  照片文件
     * @return success or fail
     */
    @PutMapping("/updateAvatar")
    @ApiOperation(value = "修改用户头像")
    public Result<String> updateAvatar(@RequestHeader("Authorization") String token, @Param("file") MultipartFile file) {
        String newLink = userInfoService.updateAvatar(token, file);
        return Result.success(ResultCodeEnum.SUCCESS, newLink);
    }

    /**
     * 模糊查询用户
     *
     * @param token    解析出用户id和手机号
     * @param username 用户名
     * @return 用户集合
     */
    @GetMapping("/queryUser")
    @ApiOperation(value = "模糊查询用户")
    public Result<List<UserVO>> queryUser(@RequestHeader("Authorization") String token, @RequestParam("username") String username) {
        List<UserVO> list = userInfoService.queryUser(token, username);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping("/getUserInfoByUserId/{userId}")
    @ApiOperation(value = "根据用户id查询用户信息")
    public Result<UserVO> getUserInfoByUserId(@PathVariable("userId") Long userId) {
        UserVO userInfoByUserId = userInfoService.getUserInfoByUserId(userId);
        return Result.success(ResultCodeEnum.SUCCESS, userInfoByUserId);
    }
}
