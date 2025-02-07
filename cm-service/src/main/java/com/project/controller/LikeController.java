package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/like")
@Api(tags = "点赞动态")
public class LikeController {
    @Resource
    private LikeService likeService;

    /**
     * 点赞或取消点赞
     */
    @GetMapping("/likeOrCancelLike")
    @ApiOperation(value = "点赞或取消点赞")
    public Result<Boolean> likeOrCancelLike(@RequestHeader("Authorization") String token, @RequestParam(value = "articleId") Long articleId) {
        boolean result = likeService.likeOrCancelLike(token, articleId);
        return Result.success(ResultCodeEnum.SUCCESS, result);
    }
}
