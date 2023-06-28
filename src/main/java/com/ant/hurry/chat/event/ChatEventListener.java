package com.ant.hurry.chat.event;

import com.ant.hurry.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ChatEventListener {

    private final ChatMessageService chatMessageService;

    @EventListener
    public void listenEventDeletedChatRoom(EventAfterDeletedChatRoom event) {
        chatMessageService.whenAfterDeletedChatRoom(event.getChatRoom());
    }
}
