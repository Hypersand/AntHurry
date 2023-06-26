package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_DELETED;
import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_SENT;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public RsData<ChatMessage> create(ChatRoom chatRoom, Member sender, String content) {
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content).build();
        chatMessageRepository.save(message).block();
        return RsData.of(MESSAGE_SENT, message);
    }

    public RsData delete(ChatMessage message) {
        chatMessageRepository.delete(message).block();
        return RsData.of(MESSAGE_DELETED);
    }

}
