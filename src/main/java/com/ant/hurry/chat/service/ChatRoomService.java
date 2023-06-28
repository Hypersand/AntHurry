package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.entity.DeletedChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import com.ant.hurry.chat.repository.DeletedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_NOT_DELETED;
import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_NO_EXISTS;
import static com.ant.hurry.chat.code.ChatRoomSuccessCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final DeletedChatRoomRepository deletedChatRoomRepository;

    public RsData<ChatRoom> findById(String id) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);

        if (chatRoom.isEmpty()) {
            return RsData.of(CHATROOM_NO_EXISTS);
        }
        ChatRoom foundchatRoom = chatRoom.get();

        List<DeletedChatRoom> deletedChatRooms = deletedChatRoomRepository.findAll();
        if (deletedChatRooms.contains(foundchatRoom)) {
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

    public RsData<List<ChatRoom>> findAllAndNotDeleted() {
        List<DeletedChatRoom> deletedChatRooms = deletedChatRoomRepository.findAll();
        List<ChatRoom> chatRooms = chatRoomRepository.findAll().stream()
                .filter(cr -> !deletedChatRooms.contains(cr)).collect(Collectors.toList());
        return RsData.of(CHATROOM_FOUND, chatRooms);
    }

    @Transactional
    public RsData<ChatRoom> create(TradeStatus tradeStatus) {
        ChatRoom chatRoom = ChatRoom.builder()
                .id(UUID.randomUUID().toString())
                .tradeStatus(tradeStatus)
                .members(List.of(tradeStatus.getRequester(), tradeStatus.getHelper()))
                .createdAt(LocalDateTime.now())
                .build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom);
        return RsData.of(CHATROOM_CREATED, insertChatRoom);
    }

    /**
     * ChatRoom을 삭제하고 DeletedChatRoom으로 변환하여 저장합니다.
     * 이벤트 리스너를 통해 삭제된 채팅방에 대한 채팅 메시지를 soft-delete합니다.
     */
    @Transactional
    public RsData delete(ChatRoom chatRoom) {
        DeletedChatRoom deletedChatRoom = DeletedChatRoom.builder()
                .tradeStatus(chatRoom.getTradeStatus())
                .members(chatRoom.getMembers())
                .build();
        deletedChatRoomRepository.insert(deletedChatRoom);
        chatRoomRepository.delete(chatRoom);



        return RsData.of(CHATROOM_DELETED);
    }

}
