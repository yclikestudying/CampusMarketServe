package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.DTO.CommentDTO;
import com.project.domain.Comment;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.CommentMapper;
import com.project.mapper.UserInfoMapper;
import com.project.service.CommentService;
import com.project.service.UserInfoService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

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
        String commentContent = commentDTO.getCommentContent();
        if (articleId <= 0 || commentContent.isEmpty()) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 把评论存入数据库
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setArticleId(articleId);
        comment.setCommentContent(commentContent);
        return commentMapper.insert(comment) > 0;
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
     * 根据文章id查询文章所有评论
     *
     * @param token
     * @param articleId
     * @return
     */
    @Override
    public List<Map<String, Object>> queryCommentByArticleId(String token, Long articleId) {
        // 1. 解析token
        getUserId(token);

        // 2. 校验文章id
        if (articleId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "文章id错误"));
        }

        // 3. 查询评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        List<Comment> comments = commentMapper.selectList(queryWrapper);

        // 4. 获取用户信息
        if (!comments.isEmpty()) {
            Set<Long> idList = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
            List<User> users = userInfoMapper.selectBatchIds(idList);
            List<Map<String, Object>> list = new ArrayList<>();
            comments.forEach(comment -> {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment.getCommentContent());
                Optional<User> optionalUser = users.stream().filter(user -> Objects.equals(user.getUserId(), comment.getUserId())).findFirst();
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    map.put("userId", user.getUserId());
                    map.put("userName", user.getUserName());
                    map.put("userAvatar", user.getUserAvatar());
                }
                list.add(map);
            });

            return list;
        }
        return null;
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
