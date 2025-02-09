package com.project.VO;

import lombok.Data;

import java.util.Date;

@Data
public class MessageVO {
    /**
     * 消息主键
     */
    private Long id;

    /**
     * 发送者id
     */
    private Long userId;

    /**
     * 发送者头像
     */
    private String userAvatar;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 发送时间
     */
    private Date createTime;
}
