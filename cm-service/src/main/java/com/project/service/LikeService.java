package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.domain.Likes;

public interface LikeService extends IService<Likes> {
    // 是否点赞
    String isLike(String token, Long articleId);
}
