package com.ant.hurry.boundedContext.coin.repository;

import com.ant.hurry.boundedContext.coin.entity.Exchange;
import com.ant.hurry.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findByStatusFalse();
    List<Exchange> findByMember(Member member);

    @Query("SELECT e FROM Exchange e JOIN FETCH e.member WHERE e.id = :exchangeId")
    Optional<Exchange> findByIdWithMember(@Param("exchangeId") Long exchangeId);
}
