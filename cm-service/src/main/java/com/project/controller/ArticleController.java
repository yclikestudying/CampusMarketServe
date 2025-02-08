package com.project.controller;

import com.project.DTO.UploadFile;
import com.project.VO.ArticleVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.domain.Article;
import com.project.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/article")
@Api(tags = "用户动态")
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * 发布动态
     *
     * @param token 用户id和手机号
     * @return success or fail
     */
    @RequestMapping("/publish")
    @ApiOperation(value = "发布动态")
    public Result<String> publish(@RequestHeader("Authorization") String token, @RequestBody(required = false) String text, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "count", required = false) Integer count, @RequestParam(value = "text", required = false) String textContent) {
        boolean result = articleService.publish(token, text, file, count, textContent);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 删除动态
     *
     * @param token 用户id和手机号
     * @return success or fail
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除动态")
    public Result<String> delete(@RequestHeader("Authorization") String token, @RequestParam("articleId") Long articleId) {
        boolean result = articleService.delete(token, articleId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询我的动态
     *
     * @param token 用户id和手机号
     * @return 动态数组
     */
    @GetMapping("/queryAll")
    @ApiOperation(value = "查询所有动态")
    public Result<Map<String, Object>> queryAll(@RequestHeader("Authorization") String token) {
        Map<String, Object> map = articleService.queryAll(token);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 查询其他人发布的动态
     *
     * @param token 用户id和手机号
     * @return 动态数组
     */
    @GetMapping("/queryOtherAll")
    @ApiOperation(value = "查询所有动态")
    public Result<Map<String, Object>> queryOtherAll(@RequestHeader("Authorization") String token) {
        Map<String, Object> map = articleService.queryOtherAll(token);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 模糊查询动态
     *
     * @param token   用户id和手机号
     * @param content 关键字
     * @return 动态集合
     */
    @GetMapping("/queryArticle")
    @ApiOperation(value = "模糊查询动态")
    public Result<List<ArticleVO>> queryArticle(@RequestHeader("Authorization") String token, @RequestParam("content") String content) {
        List<ArticleVO> list = articleService.queryArticle(token, content);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 根据用户id查询动态
     *
     * @param userId 用户id
     * @return 动态集合
     */
    @GetMapping("/queryArticleByUserId/{userId}")
    @ApiOperation(value = "根据用户id查询动态")
    public Result<Map<String, Object>> queryArticleByUserId(@RequestHeader("Authorization") String token, @PathVariable("userId") Long userId) {
        Map<String, Object> map = articleService.queryArticleByUserId(token, userId);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 根据文章id查询动态
     */
    @GetMapping("/queryArticleByArticleId/{articleId}")
    @ApiOperation(value = "根据文章id查询动态")
    public Result<List<Object>> queryArticleByArticleId(@PathVariable("articleId") Long articleId) {
        List<Object> list = articleService.queryArticleByArticleId(articleId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
