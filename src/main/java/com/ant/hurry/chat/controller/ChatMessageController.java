package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/room/{id}/message")
    public void message(@Payload ChatMessageDto dto) {
        RsData<ChatMessage> rs = chatMessageService.create(dto);
        ChatMessage message = rs.getData();
        messagingTemplate.convertAndSend("/sub/chat/room/%s".formatted(dto.getChatRoom().getId()), message);
    }

}
