package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ant.hurry.chat.code.ChatMessageErrorCode.MESSAGE_NOT_DELETED;
import static com.ant.hurry.chat.code.ChatMessageSuccessCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional(readOnly = true)
    public RsData<List<ChatMessage>> findByChatRoom(ChatRoom chatRoom) {
        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessageByChatRoom(chatRoom);
        return RsData.of(MESSAGE_FOUND, chatMessages);
    }

    public RsData<ChatMessage> create(ChatMessageDto dto) {
        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .chatRoom(dto.getChatRoom())
                .sender(dto.getSender())
                .content(dto.getContent())
                .build();
        chatMessageRepository.save(message);
        dto.getChatRoom().setLatestMessage(message);
        return RsData.of(MESSAGE_SENT, message);
    }

    public RsData deleteSoft(ChatMessage chatMessage) {
        ChatMessage deletedChatMessage = chatMessageRepository.deleteSoft(chatMessage);

        if (deletedChatMessage.getDeletedAt() == null) {
            return RsData.of(MESSAGE_NOT_DELETED);
        }

        return RsData.of(MESSAGE_DELETED);
    }

    public RsData delete(ChatMessage message) {
        chatMessageRepository.delete(message);
        return RsData.of(MESSAGE_DELETED);
    }

    public void whenAfterDeletedChatRoom(ChatRoom chatRoom) {
        List<ChatMessage> chatMessages = findByChatRoom(chatRoom).getData();
        chatMessages.forEach(chatMessageRepository::deleteSoft);
    }

}
