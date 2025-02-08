package com.project.controller;

import com.project.DTO.CommentDTO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Api(tags = "动态评论")
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 根据文章id查询文章所有评论
     */
    @GetMapping("/queryCommentByArticleId/{articleId}")
    @ApiOperation(value = "根据文章id查询文章所有评论")
    public Result<List<Map<String, Object>>> queryCommentByArticleId(@RequestHeader("Authorization") String token, @PathVariable(value = "articleId") Long articleId) {
        List<Map<String, Object>> list = commentService.queryCommentByArticleId(token, articleId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }


    /**
     * 发表评论
     *
     * @param token      用户id和手机号
     * @param commentDTO 评论数据
     * @return true or fail
     */
    @PostMapping("/publish")
    @ApiOperation(value = "发表评论")
    public Result<String> publish(@RequestHeader("Authorization") String token, @RequestBody CommentDTO commentDTO) {
        return commentService.publish(token, commentDTO) ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 删除评论
     *
     * @param token     用户id和手机号
     * @param commentId 评论id
     * @return true or fail
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除评论")
    public Result<String> delete(@RequestHeader("Authorization") String token, @RequestParam("commentId") Long commentId) {
        return commentService.delete(token, commentId) ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

}
