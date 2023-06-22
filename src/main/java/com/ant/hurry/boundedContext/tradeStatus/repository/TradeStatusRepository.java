package com.ant.hurry.boundedContext.tradeStatus.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeStatusRepository extends JpaRepository<TradeStatus, Long> {
}
