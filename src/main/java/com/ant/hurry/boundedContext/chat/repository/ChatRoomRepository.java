package com.ant.hurry.boundedContext.chat.repository;

import com.ant.hurry.boundedContext.chat.entity.ChatRoom;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.tradeStatus IN :tradeStatuses")
    List<ChatRoom> findByTradeStatus(@Param("tradeStatuses") List<TradeStatus> tradeStatuses);
}
