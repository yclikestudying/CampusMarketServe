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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Likes>
        implements LikeService {
    @Resource
    private LikeMapper likeMapper;

    /**
     * 查看文章是否点赞
     *
     * @param token
     * @param articleIds
     * @return
     */
    @Override
    public Map<Long, Boolean> isLike(String token, List<Long> articleIds) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 校验参数
        if (articleIds.isEmpty()) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 查看是否已经点赞
        Map<Long, Boolean> likeMap = new HashMap<>();
        articleIds.forEach(articleId -> {
            QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("article_id", articleId);
            Likes likes = likeMapper.selectOne(queryWrapper);
            likeMap.put(articleId, likes != null);
        });

        return likeMap;
    }

    /**
     * 点赞或取消点赞
     *
     * @param token
     * @param articleId
     * @return
     */
    @Override
    public boolean likeOrCancelLike(String token, Long articleId) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 校验参数
        if (articleId == null || articleId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 查询是否点赞
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("article_id", articleId);
        Likes one = likeMapper.selectOne(queryWrapper);
        if (one == null) {
            // 没有点赞
            Likes likes = new Likes();
            likes.setUserId(userId);
            likes.setArticleId(articleId);
            return likeMapper.insert(likes) > 0;
        }

        // 取消点赞
        return likeMapper.delete(queryWrapper) > 0;
    }

    /**
     * 查看文章的点赞数
     *
     * @param token
     * @param articleIds
     * @return
     */
    @Override
    public Map<Long, Integer> getLikeNumber(String token, List<Long> articleIds) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 校验参数
        if (articleIds.isEmpty()) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 查看是否已经点赞
        Map<Long, Integer> likeNumberMap = new HashMap<>();
        articleIds.forEach(articleId -> {
            QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleId);
            Integer count = likeMapper.selectCount(queryWrapper);
            likeNumberMap.put(articleId, count);
        });
        return likeNumberMap;
    }
}
