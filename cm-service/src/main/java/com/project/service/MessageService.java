package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.MessageVO;
import com.project.domain.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    // 存储发送的聊天信息
    boolean saveMessage(String content, Long userId, Long otherUserId);

    List<MessageVO> getAllMessage(Long userId, Long otherUserId);
}
