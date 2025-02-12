package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.VO.MessageListVO;
import com.project.VO.MessageVO;
import com.project.domain.Message;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.MessageMapper;
import com.project.mapper.UserInfoMapper;
import com.project.service.FollowsService;
import com.project.service.MessageService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private FollowsService followsService;

    /**
     * 存储发送的聊天信息
     *
     * @param content
     * @param userId
     * @param otherUserId
     * @return
     */
    @Override
    public boolean saveMessage(String content, Long userId, Long otherUserId) {
        // 1. 校验参数
        if (StringUtils.isBlank(content)) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "内容不能为空"));
        }

        if (userId == null || otherUserId == null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "内容不能为空"));
        }

        if (userId <= 0 || otherUserId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 2. 把信息存入数据库
        Message message = new Message();
        message.setContent(content);
        message.setUserId(userId);
        message.setOtherUserId(otherUserId);
        return messageMapper.insert(message) > 0;
    }

    /**
     * 获取发送消息
     *
     * @param userId
     * @param otherUserId
     * @return
     */
    @Override
    public List<MessageVO> getAllMessage(Long userId, Long otherUserId) {
        // 1. 校验
        if (userId <= 0 || otherUserId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 2. 查询聊天记录
        List<Message> messages = messageMapper.selectList(
                new QueryWrapper<Message>()
                        .select("id", "user_id", "content", "create_time")
                        .and(wrapper -> wrapper
                                .eq("user_id", userId).eq("other_user_id", otherUserId)
                                .or()
                                .eq("user_id", otherUserId).eq("other_user_id", userId)
                        )
                        .orderByAsc("create_time")
        );

        // 3. 根据每一条聊天记录查询发送者用户的相关信息
        List<MessageVO> list = new ArrayList<>();
        if (!messages.isEmpty()) {
            messages.forEach(message -> {
                MessageVO messageVO = new MessageVO();
                BeanUtils.copyProperties(message, messageVO);
                messageVO.setUserAvatar(userInfoMapper.selectById(message.getUserId()).getUserAvatar());
                list.add(messageVO);
            });
            return list;
        }
        return null;
    }

    /**
     * 对未读消息进行已读处理
     *
     * @param token
     * @return
     */
    @Override
    public boolean readMessage(String token, Long otherUserId) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 查询出未读消息
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", otherUserId)
                .eq("other_user_id", userId)
                .eq("is_read", 0);
        List<Message> messages = messageMapper.selectList(queryWrapper);

        // 3. 设置未读消息为已读
        AtomicInteger count = new AtomicInteger(0);
        if (!messages.isEmpty()) {
            messages.forEach(message -> {
                message.setIsRead(1);
                int i = messageMapper.updateById(message);
                if (i >= 0) {
                    count.incrementAndGet();
                }
            });
            return count.get() == messages.size();
        }

        return true;
    }

    /**
     * 获取所有未读消息数量
     *
     * @param token
     * @return
     */
    @Override
    public Integer getBadge(String token) {
        // 1. 解析token
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 查询未读消息数量
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("other_user_id", userId)
                .eq("is_read", 0);
        return messageMapper.selectCount(queryWrapper);
    }

    /**
     * 查询与用户的消息列表框
     *
     * @param token
     * @return
     */
    @Override
    public List<MessageListVO> getUserMessageList(String token) {
        // 1. 解析token， 获取自己的userId
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        Long userId = (Long) stringObjectMap.get("userId");

        // 2. 获取用户
        // 我关注的
        List<User> users = followsService.followerUser(token, null);
        // 关注我的
        List<User> users1 = followsService.followeeUser(token, null);
        // 取并集
        Set<User> collect = null;
        if (!users.isEmpty() && !users1.isEmpty()) {
            users.addAll(users1);
            collect = new HashSet<>(users);
        } else if (!users.isEmpty()) {
            collect = new HashSet<>(users);
        } else if (!users1.isEmpty()) {
            collect = new HashSet<>(users1);
        } else {
            return null;
        }

        // 3. 根据用户id获取到与当前用户的未读消息数量以及最后一条消息
        // 有未读消息的消息框
        List<MessageListVO> unReadList = new ArrayList<>();
        // 已读消息的消息框
        List<MessageListVO> readList = new ArrayList<>();
        collect.forEach(user -> {
            // 存入用户id、用户头像、用户名称
            MessageListVO messageListVO = new MessageListVO();
            messageListVO.setUserId(user.getUserId());
            messageListVO.setUserAvatar(user.getUserAvatar());
            messageListVO.setUserName(user.getUserName());

            // 根据自己id与其他用户id查询最后一条展示的聊天消息
            List<MessageVO> allMessage = this.getAllMessage(userId, user.getUserId());
            MessageVO messageVO = allMessage.get(allMessage.size() - 1);
            messageListVO.setLastMessage(messageVO.getContent());
            messageListVO.setCreateTime(messageVO.getCreateTime());

            // 根据自己id与其他用户id查询未读消息数量
            QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", user.getUserId())
                    .eq("other_user_id", userId)
                    .eq("is_read", 0);
            Integer count = messageMapper.selectCount(queryWrapper);
            messageListVO.setUnReadMessageCount(count);
            if (count != 0) {
                unReadList.add(messageListVO);
            } else {
                readList.add(messageListVO);
            }
        });

        unReadList.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        readList.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        unReadList.addAll(readList);
        return unReadList;
    }
}
