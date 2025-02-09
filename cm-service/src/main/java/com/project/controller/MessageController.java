package com.project.controller;

import com.project.VO.MessageVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.MessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
