package com.ant.hurry.boundedContext.coin.repository;

import com.ant.hurry.boundedContext.coin.entity.CoinLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinLogRepository extends JpaRepository<CoinLog, Long> {
}
