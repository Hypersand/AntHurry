package com.ant.hurry.boundedContext.coin.repository;

import com.ant.hurry.boundedContext.coin.entity.CoinChargeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinChargeLogRepository extends JpaRepository<CoinChargeLog, Long> {
}
