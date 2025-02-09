package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.VO.MessageVO;
import com.project.domain.Message;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.MessageMapper;
import com.project.mapper.UserInfoMapper;
import com.project.service.MessageService;
import com.project.util.ThrowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

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
}
