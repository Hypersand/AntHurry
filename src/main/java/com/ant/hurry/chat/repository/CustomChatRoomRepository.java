package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomChatRoomRepository {

    Flux<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses);

    Flux<ChatRoom> findAllAndDeletedAtIsNull();

    Mono<ChatRoom> deleteSoft(ChatRoom chatRoom);
}
