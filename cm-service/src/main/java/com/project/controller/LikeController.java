package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/like")
@Api(tags = "点赞动态")
public class LikeController {
    @Resource
    private LikeService likeService;

    /**
     * 点赞或取消点按
     * @param token 用户id和手机号
     * @param articleId 动态id
     * @return success or fail
     */
    @GetMapping("/isLike")
    @ApiOperation(value = "是否点赞")
    public Result<String> isLike(@RequestHeader("Authorization") String token, @RequestParam("articleId") Long articleId) {
        return Result.success(ResultCodeEnum.SUCCESS, likeService.isLike(token, articleId));
    }
}
