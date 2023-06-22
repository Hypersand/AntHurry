package com.ant.hurry.boundedContext.chat.controller;

import com.ant.hurry.boundedContext.chat.entity.ChatMessage;
import com.ant.hurry.boundedContext.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        messagingTemplate.convertAndSend("/sub/chat/room/%d".formatted(message.getChatRoomId()));
    }

}
