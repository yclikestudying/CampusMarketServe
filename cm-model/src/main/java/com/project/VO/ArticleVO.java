package com.project.VO;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleVO {
    /**
     * 动态id
     */
    private Long articleId;

    /**
     * 动态内容
     */
    private String articleContent;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 发表时间
     */
    private Date createTime;
}
