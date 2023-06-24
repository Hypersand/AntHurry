package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, Long> {
    @Query("{'tradeStatus': {$in: ?0}}")
    List<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses);

    @Query("{$currentDate: {'deletedAt': true} }")
    @Modifying
    void deleteSoftly(ChatRoom chatRoom);
}
