package com.ant.hurry.boundedContext.tradeStatus.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeStatusRepository extends JpaRepository<TradeStatus, Long> {
    List<TradeStatus> findByRequester(Member member);
}
