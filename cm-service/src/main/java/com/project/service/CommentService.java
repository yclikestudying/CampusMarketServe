package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.DTO.CommentDTO;
import com.project.domain.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {
    // 发表评论
    boolean publish(String token, CommentDTO commentDTO);

    // 删除评论
    boolean delete(String token, Long commentId);

    // 根据文章id查询文章所有评论
    List<Map<String, Object>> queryCommentByArticleId(String token, Long articleId);
}
