package com.project.VO;

import lombok.Data;

@Data
public class UserVO {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户性别
     */
    private Integer userGender;
}
