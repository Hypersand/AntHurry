package com.ant.hurry.boundedContext.chat.repository;

import com.ant.hurry.boundedContext.chat.entity.ChatRoom;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.tradeStatus IN :tradeStatuses")
    List<ChatRoom> findByTradeStatus(@Param("tradeStatuses") List<TradeStatus> tradeStatuses);

    @Query("select c from ChatRoom c join fetch c.tradeStatus.requester r join fetch c.tradeStatus.helper h where r.id = :memberId or h.id = :memberId")
    List<ChatRoom> findByMemberId(@Param("memberId") Long id);
}
