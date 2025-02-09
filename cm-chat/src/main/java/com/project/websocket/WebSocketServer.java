package com.project.websocket;

import com.google.gson.Gson;
import com.project.DTO.MessageDTO;
import com.project.service.MessageService;
import com.project.service.impl.MessageServiceImpl;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/wsConnect/{userId}")
@Component
@Slf4j
public class WebSocketServer {
    // 记录当前连接数
    private static int onlineCount = 0;

    // 存放每个客户端对应的 websocket 对象
    private static final ConcurrentHashMap<Long, Session> webSocketMap = new ConcurrentHashMap<>();

    // 接收 userId
    private Long userId;
    private static ApplicationContext applicationContext;
    private final Gson gson = new Gson();
    private MessageService messageService;

    // 客户端与服务器建立连接
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        if (!webSocketMap.containsKey(userId)) {
            webSocketMap.put(userId, session);
        }
        this.userId = userId;
        messageService = applicationContext.getBean(MessageService.class);
        log.info("用户:{} 成功建立连接", userId);
    }

    // 客户端发送消息
    @OnMessage
    public void onMessage(String chatContent) throws IOException {
        MessageDTO messageDTO = gson.fromJson(chatContent, MessageDTO.class);
        boolean result = messageService.saveMessage(messageDTO.getMessage(), messageDTO.getUserId(), messageDTO.getOtherUserId());
        webSocketMap.get(userId).getBasicRemote().sendText(result ? "成功" : "失败");
    }

    // 客户端关闭连接
    @OnClose
    public void onClose() {
        System.out.println("用户" + userId + "关闭连接");
        webSocketMap.remove(userId);
    }
//
//    // 服务器更新一对一聊天记录
//    public void sendMessage(Long fromId, Long toId) throws IOException {
//        messageService = applicationContext.getBean(MessageService.class);
//        List<Message> messageList = messageService.getMessage(fromId, toId);
//        log.info("用户{}给用户{}发送消息", fromId, toId);
//        System.out.println(webSocketMap);
//        if (webSocketMap.containsKey(fromId)) {
//            webSocketMap.get(fromId).getBasicRemote().sendText(gson.toJson(messageList));
//        } else {
//            log.info("用户{}不在线", fromId);
//        }
//        if (webSocketMap.containsKey(toId)) {
//            webSocketMap.get(toId).getBasicRemote().sendText(gson.toJson(messageList));
//        } else {
//            log.info("用户{}不在线", toId);
//        }
//    }
//
//    // 获取当前人数
//    public static synchronized int getOnlineCount() {
//        return onlineCount;
//    }
//
//    // 当前人数 + 1
//    public static synchronized void addOnlineCount() {
//        WebSocketServer.onlineCount++;
//    }
//
//    // 当前人数 - 1
//    public static synchronized void subOnlineCount() {
//        WebSocketServer.onlineCount--;
//    }
//
//    public static void setApplicationContext(ApplicationContext context) {
//        applicationContext = context;
//    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
}
