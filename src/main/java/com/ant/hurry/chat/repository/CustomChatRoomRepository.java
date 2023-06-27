package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;

import java.util.List;

public interface CustomChatRoomRepository {

    List<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses);

    List<ChatRoom> findAllAndDeletedAtIsNull();

    ChatRoom deleteSoft(ChatRoom chatRoom);
}
