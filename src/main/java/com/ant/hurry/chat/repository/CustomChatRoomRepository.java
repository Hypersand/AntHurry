package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface CustomChatRoomRepository {
    List<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses);

    Optional<ChatRoom> findByTradeStatusId(Long id);

    void updateStatusOfChatRoom(ChatRoom chatRoom, String status);

    void deleteMembers(ChatRoom chatRoom, Member member);
}
