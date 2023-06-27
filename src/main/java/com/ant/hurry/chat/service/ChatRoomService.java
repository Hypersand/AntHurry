package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_NOT_DELETED;
import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_NO_EXISTS;
import static com.ant.hurry.chat.code.ChatRoomSuccessCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public RsData<ChatRoom> findById(String id) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);

        if (chatRoom.isEmpty()) {
            return RsData.of(CHATROOM_NO_EXISTS);
        }
        ChatRoom foundchatRoom = chatRoom.get();

        if (foundchatRoom.getDeletedAt() != null) {
            return RsData.of(CHATROOM_NO_EXISTS);
        }

        return RsData.of(CHATROOM_FOUND, foundchatRoom);
    }

    public RsData<List<ChatRoom>> findByTradeStatus(List<TradeStatus> tradeStatuses) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByTradeStatus(tradeStatuses);
        return RsData.of(CHATROOM_FOUND, chatRooms);
    }

    public RsData<List<ChatRoom>> findAll() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return RsData.of(CHATROOM_FOUND, chatRooms);
    }

    public RsData<List<ChatRoom>> findAllAndDeletedAtIsNull() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllAndDeletedAtIsNull();
        return RsData.of(CHATROOM_FOUND, chatRooms);
    }

    @Transactional
    public RsData<ChatRoom> create(TradeStatus tradeStatus) {
        ChatRoom chatRoom = ChatRoom.builder()
                .id(UUID.randomUUID().toString())
                .tradeStatus(tradeStatus)
                .createdAt(LocalDateTime.now())
                .build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom);
        return RsData.of(CHATROOM_CREATED, insertChatRoom);
    }

    @Transactional
    public RsData deleteSoft(ChatRoom chatRoom) {
        ChatRoom deletedChatRoom = chatRoomRepository.deleteSoft(chatRoom);

        if (deletedChatRoom.getDeletedAt() == null) {
            return RsData.of(CHATROOM_NOT_DELETED);
        }

        return RsData.of(CHATROOM_DELETED);
    }

    @Transactional
    public RsData delete(ChatRoom chatRoom) {
        chatRoomRepository.delete(chatRoom);
        return RsData.of(CHATROOM_DELETED);
    }

}
