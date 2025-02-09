package com.project.DTO;

import lombok.Data;

@Data
public class MessageDTO {
    /**
     * 消息内容
     */
    private String message;

    /**
     * 自己id
     */
    private Long userId;

    /**
     * 对方用户id
     */
    private Long otherUserId;
}
