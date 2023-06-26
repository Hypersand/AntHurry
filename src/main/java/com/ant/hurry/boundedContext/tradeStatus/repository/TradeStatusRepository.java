package com.ant.hurry.boundedContext.tradeStatus.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TradeStatusRepository extends JpaRepository<TradeStatus, Long> {
    @Query("select t from TradeStatus t join fetch t.requester r join fetch t.helper h where r.id = :memberId or h.id = :memberId")
    List<TradeStatus> findByRequesterOrHelper(@Param("memberId") Long memberId);

    @Query("select t from TradeStatus t join fetch t.requester r join fetch t.helper h where t.id = :id")
    Optional<TradeStatus> findById(@Param("id") Long id);
}
