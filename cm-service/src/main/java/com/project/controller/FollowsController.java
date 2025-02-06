package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.domain.User;
import com.project.service.FollowsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/follows")
@Api(tags = "用户关注")
public class FollowsController {
    @Resource
    private FollowsService followsService;

    /**
     * 关注用户
     *
     * @param token
     * @param otherUserId 被关注者id
     * @return success or fail
     */
    @GetMapping("/follow")
    @ApiOperation(value = "关注其他用户")
    public Result<String> follow(@RequestHeader("Authorization") String token, @RequestParam(value = "otherUserId") Long otherUserId) {
        boolean result = followsService.follow(token, otherUserId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/cancelFollow")
    @ApiOperation(value = "取消关注")
    public Result<String> cancelFollow(@RequestHeader("Authorization") String token, @RequestParam(value = "otherUserId") Long otherUserId) {
        boolean result = followsService.cancelFollow(token, otherUserId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询我的关注
     */
    @GetMapping("/followerUser")
    @ApiOperation(value = "查询我的关注")
    public Result<List<User>> followerUser(@RequestHeader("Authorization") String token) {
        List<User> users = followsService.followerUser(token);
        return Result.success(ResultCodeEnum.SUCCESS, users);
    }

    /**
     * 查询我的粉丝
     */
    @GetMapping("/followeeUser")
    @ApiOperation(value = "查询我的粉丝")
    public Result<List<User>> followeeUser(@RequestHeader("Authorization") String token) {
        List<User> users = followsService.followeeUser(token);
        return Result.success(ResultCodeEnum.SUCCESS, users);
    }

    /**
     * 查询互关
     */
    @GetMapping("/eachFollow")
    @ApiOperation(value = "查询我的粉丝")
    public Result<List<User>> eachFollow(@RequestHeader("Authorization") String token) {
        List<User> users = followsService.eachFollow(token);
        return Result.success(ResultCodeEnum.SUCCESS, users);
    }

    /**
     * 根据id查询是否关注
     */
    @GetMapping("/isFollow")
    @ApiOperation(value = "查询是否关注")
    public Result<String> isFollow(@RequestHeader("Authorization") String token, @RequestParam(value = "otherUserId") Long otherUserId) {
        boolean result = followsService.isFollow(token, otherUserId);
        return Result.success(ResultCodeEnum.SUCCESS, result ? "已关注" : "关注");
    }

}
