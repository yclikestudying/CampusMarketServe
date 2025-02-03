package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.DTO.CommentDTO;
import com.project.domain.Comment;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.CommentMapper;
import com.project.service.CommentService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    private Long userId;

    /**
     * 发表评论
     *
     * @param token      用户id和手机号
     * @param commentDTO 评论数据
     * @return true or fail
     */
    @Override
    public boolean publish(String token, CommentDTO commentDTO) {
        // 1. 解析token
        getUserId(token);

        // 2. 校验评论数据
        Long articleId = commentDTO.getArticleId();
        Long commentParentId = commentDTO.getCommentParentId();
        String commentContent = commentDTO.getCommentContent();
        if ((articleId == null || articleId <= 0) || commentParentId == null || commentParentId < 0 || commentContent.isEmpty()) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 存储数据
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setCommentParentId(commentParentId);
        comment.setCommentContent(commentContent);
        return commentMapper.insert(comment) == 1;
    }

    /**
     * 删除评论
     *
     * @param token     用户id和手机号
     * @param commentId 评论id
     * @return true or fail
     */
    @Override
    public boolean delete(String token, Long commentId) {
        // 1. 解析token
        getUserId(token);

        // 2. 校验数据
        if (commentId == null || commentId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 查询评论是否存在
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", commentId)
                .eq("user_id", userId);
        Comment comment = commentMapper.selectOne(queryWrapper);
        if (comment == null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "评论不存在"));
        }

        // 4. 删除评论
        return commentMapper.deleteById(commentId) == 1;
    }

    /**
     * 解析token
     *
     * @param token 用户id和手机号
     */
    private void getUserId(String token) {
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        userId = (Long) stringObjectMap.get("userId");
    }
}
