package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.domain.Likes;

import java.util.List;
import java.util.Map;

public interface LikeService extends IService<Likes> {

    // 查看文章是否点赞
    Map<Long, Boolean> isLike(String token, List<Long> articleIds);

    // 点赞或取消点赞
    boolean likeOrCancelLike(String token, Long articleId);

    // 查看文章的点赞数
    Map<Long, Integer> getLikeNumber(String token, List<Long> articleIds);
}
