package com.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 关系表
 * @TableName follows
 */
@TableName(value ="follows")
@Data
public class Follows implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关注者id
     */
    private Long followerId;

    /**
     * 被关注者id
     */
    private Long followeeId;

    /**
     * 建立关系时间
     */
    private Date createTime;

    /**
     * 0-未删除，1-删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}