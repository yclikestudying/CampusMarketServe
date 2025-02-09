package com.project.VO;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfoVO {
    /**
     * id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 性别
     */
    private Integer userGender;

    /**
     * 生日
     */
    private String userBirthday;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 所在地
     */
    private String userLocation;

    /**
     * 家乡
     */
    private String userHometown;

    /**
     * 专业
     */
    private String userProfession;

    /**
     * 标签
     */
    private String userTags;

    /**
     * 创建时间
     */
    private Date createTime;
}
