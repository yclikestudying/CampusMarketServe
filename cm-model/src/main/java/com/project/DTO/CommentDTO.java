package com.project.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    /**
     * 评论的动态id
     */
    private Long articleId;

    /**
     * 评论的父id
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String commentContent;
}
