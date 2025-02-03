package com.project.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    /**
     * 评论的动态
     */
    private Long articleId;

    /**
     * 评论的父id
     */
    private Long commentParentId;

    /**
     * 评论内容
     */
    private String commentContent;
}
