package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.DTO.CommentDTO;
import com.project.domain.Comment;

public interface CommentService extends IService<Comment> {
    // 发表评论
    boolean publish(String token, CommentDTO commentDTO);

    // 删除评论
    boolean delete(String token, Long commentId);
}
