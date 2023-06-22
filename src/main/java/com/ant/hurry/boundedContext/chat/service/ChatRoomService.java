package com.ant.hurry.boundedContext.chat.service;

import com.ant.hurry.boundedContext.chat.entity.ChatRoom;
import com.ant.hurry.boundedContext.chat.repository.ChatRoomRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom create(TradeStatus tradeStatus) {
        ChatRoom chatRoom = ChatRoom.builder()
                .tradeStatus(tradeStatus)
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    public ChatRoom findById(Long id) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);
        if (chatRoom.isEmpty()) {
//            [ErrorCode] 존재하지 않는 채팅방입니다.
        }
        return chatRoom.get();
    }

    public List<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses) {
        return chatRoomRepository.findByTradeStatus(tradeStatuses);
    }

}
