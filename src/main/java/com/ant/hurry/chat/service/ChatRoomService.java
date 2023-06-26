package com.ant.hurry.chat.service;

import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoom create(TradeStatus tradeStatus) {
        ChatRoom chatRoom = ChatRoom.builder()
                .tradeStatus(tradeStatus)
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    public ChatRoom findById(String id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).block();
        if (chatRoom == null || chatRoom.getDeletedAt() != null) {
//            [ErrorCode] 존재하지 않는 채팅방입니다.
        }
        return chatRoom;
    }

    public List<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses) {
        return chatRoomRepository.findByTradeStatus(tradeStatuses);
    }

    @Transactional
    public void delete(ChatRoom chatRoom) {
        chatRoomRepository.deleteSoftly(chatRoom);
    }

}
