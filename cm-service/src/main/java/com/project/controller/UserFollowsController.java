package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.UserFollowsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/follow")
public class UserFollowsController {
    @Resource
    private UserFollowsService userFollowsService;

    /**
     * 关注用户
     *
     * @param token  用户id和手机号
     * @param userId 被关注者id
     * @return true or false
     */
    @GetMapping("/addUser/{userId}")
    @ApiOperation(value = "关注用户")
    public Result<String> addUser(@RequestHeader("Authorization") String token, @PathVariable("userId") Long userId) {
        boolean result = userFollowsService.addUser(token, userId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 取消关注
     *
     * @param token  用户id和手机号
     * @param userId 被关注者id
     * @return true or false
     */
    @DeleteMapping("/deleteUser/{userId}")
    @ApiOperation(value = "取消关注")
    public Result<String> deleteUser(@RequestHeader("Authorization") String token, @PathVariable("userId") Long userId) {
        boolean result = userFollowsService.deleteUser(token, userId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询当前一个用户是否被关注
     *
     * @param token      用户id和手机号
     * @param userId 被关注者id
     * @return true or false
     */
    @GetMapping("/queryUserFollow/{userId}")
    @ApiOperation(value = "查询当前一个用户是否被关注")
    public Result<Boolean> queryUserFollow(@RequestHeader("Authorization") String token, @PathVariable("userId") Long userId) {
        boolean flag = userFollowsService.queryUserFollow(token, userId);
        return Result.success(ResultCodeEnum.SUCCESS, flag);
    }

    /**
     * 查询互关、关注以及粉丝
     */
    @GetMapping("/queryUserFollowData")
    @ApiOperation(value = "查询互关、关注以及粉丝")
    public Result<List<List<Long>>> queryUserFollowData(@RequestHeader("Authorization") String token) {
        List<List<Long>> list = userFollowsService.queryUserFollowData(token);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
