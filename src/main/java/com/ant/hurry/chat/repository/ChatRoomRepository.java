package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {

    @Query("{'tradeStatus': {$in: ?0}}")
    List<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses);

    @Query("{'deletedAt': null}")
    Flux<ChatRoom> findAll();
}
