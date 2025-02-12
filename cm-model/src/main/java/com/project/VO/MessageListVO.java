package com.project.VO;

import lombok.Data;

import java.util.Date;

/**
 * 消息列表的展示
 */
@Data
public class MessageListVO {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 最后一条聊天消息
     */
    private String lastMessage;

    /**
     * 聊天时间
     */
    private Date createTime;

    /**
     * 未读消息数量
     */
    private Integer unReadMessageCount;
}
