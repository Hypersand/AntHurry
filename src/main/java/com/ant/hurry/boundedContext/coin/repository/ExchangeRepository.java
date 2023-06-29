package com.ant.hurry.boundedContext.coin.repository;

import com.ant.hurry.boundedContext.coin.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
