package com.ant.hurry.boundedContext.coin.repository;

import com.ant.hurry.boundedContext.coin.entity.CoinChargeLog;
import com.ant.hurry.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoinChargeLogRepository extends JpaRepository<CoinChargeLog, Long> {
    @EntityGraph(attributePaths = {"member", "exchange"})
    List<CoinChargeLog> findByMember(Member member);
}
