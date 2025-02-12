package com.project.controller;

import com.project.VO.MessageListVO;
import com.project.VO.MessageVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.MessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    /**
     * 获取私聊的聊天记录
     */
    @GetMapping("/getAllMessage")
    @ApiOperation(value = "获取私聊的聊天记录")
    public Result<List<MessageVO>> getAllMessage(@RequestParam("userId") Long userId, @RequestParam("otherUserId") Long otherUserId) {
        List<MessageVO> list = messageService.getAllMessage(userId, otherUserId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 对未读消息进行已读处理
     */
    @PutMapping("/readMessage")
    @ApiOperation(value = "对未读消息进行已读处理")
    public Result<Boolean> readMessage(@RequestHeader("Authorization") String token, @RequestParam("otherUserId") Long otherUserId) {
        boolean result = messageService.readMessage(token, otherUserId);
        return Result.success(ResultCodeEnum.SUCCESS, result);
    }

    /**
     * 获取所有未读消息
     */
    @GetMapping("/getBadge")
    @ApiOperation(value = "获取所有未读消息")
    public Result<Integer> getBadge(@RequestHeader("Authorization") String token) {
        Integer count = messageService.getBadge(token);
        return Result.success(ResultCodeEnum.SUCCESS, count);
    }

    /**
     * 查询与用户的消息列表框
     */
    @GetMapping("/getUserMessageList")
    @ApiOperation(value = "查询与用户的消息列表框")
    public Result<List<MessageListVO>> getUserMessageList(@RequestHeader("Authorization") String token) {
        List<MessageListVO> messageList = messageService.getUserMessageList(token);
        return Result.success(ResultCodeEnum.SUCCESS, messageList);
    }
}
