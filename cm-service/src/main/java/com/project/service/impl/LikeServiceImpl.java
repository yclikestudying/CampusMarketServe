package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.domain.Article;
import com.project.domain.Likes;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.ArticleMapper;
import com.project.mapper.LikeMapper;
import com.project.service.LikeService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Likes>
        implements LikeService {
    @Resource
    private LikeMapper likeMapper;

    private Long userId;

    /**
     * 点赞或取消点按
     *
     * @param token     用户id和手机号
     * @param articleId 动态id
     * @return success or fail
     */
    @Override
    public String isLike(String token, Long articleId) {
        // 1. 解析token
        getUserId(token);

        // 2. 校验articleId是否符合要求
        if (articleId == null || articleId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 查询是否已经点过赞
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("article_id", articleId);
        Likes like = likeMapper.selectOne(queryWrapper);
        if (like == null) {
            // 没有点过赞，则进行点赞
            Likes likes = new Likes();
            likes.setUserId(userId);
            likes.setArticleId(articleId);

            return likeMapper.insert(likes) == 1 ? "点赞成功" : "点赞失败";
        }
        // 点过赞，则取消点赞
        return likeMapper.delete(queryWrapper) == 1 ? "取消点赞成功" : "取消点赞失败";
    }

    /**
     * 解析token
     *
     * @param token 用户id和手机号
     */
    private void getUserId(String token) {
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        userId = (Long) stringObjectMap.get("userId");
    }
}
