package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.code.ChatMessageErrorCode;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

import static com.ant.hurry.chat.code.ChatMessageErrorCode.*;
import static com.ant.hurry.chat.code.ChatMessageSuccessCode.*;
import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_NOT_DELETED;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public RsData<List<ChatMessage>> findByChatRoom(ChatRoom chatRoom) {
        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessageByChatRoom(chatRoom);
        return RsData.of(MESSAGE_FOUND, chatMessages);
    }

    @Transactional
    public RsData<ChatMessage> create(ChatRoom chatRoom, Member sender, String content) {
        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content).build();
        chatMessageRepository.save(message);
        chatRoom.setLatestMessage(message);
        return RsData.of(MESSAGE_SENT, message);
    }

    @Transactional
    public RsData deleteSoft(ChatMessage chatMessage) {
        ChatMessage deletedChatMessage = chatMessageRepository.deleteSoft(chatMessage);

        if(deletedChatMessage.getDeletedAt() == null) {
            return RsData.of(MESSAGE_NOT_DELETED);
        }

        return RsData.of(MESSAGE_DELETED);
    }

    @Transactional
    public RsData delete(ChatMessage message) {
        chatMessageRepository.delete(message);
        return RsData.of(MESSAGE_DELETED);
    }

}
