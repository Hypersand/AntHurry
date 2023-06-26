package com.ant.hurry.chat.service;

import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage create(ChatRoom chatRoom, Member sender, String content) {
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content).build();
        chatMessageRepository.save(message);
        return message;
    }
}
