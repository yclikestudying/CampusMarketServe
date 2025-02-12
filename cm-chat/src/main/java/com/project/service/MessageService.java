package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.MessageListVO;
import com.project.VO.MessageVO;
import com.project.domain.Message;

import java.util.List;
import java.util.Map;

public interface MessageService extends IService<Message> {
    // 存储发送的聊天信息
    boolean saveMessage(String content, Long userId, Long otherUserId);

    List<MessageVO> getAllMessage(Long userId, Long otherUserId);

    // 读未读消息进行已读处理
    boolean readMessage(String token, Long otherUserId);

    // 获取所有未读消息
    Integer getBadge(String token);

    // 查询与用户的消息列表框
    List<MessageListVO> getUserMessageList(String token);
}
