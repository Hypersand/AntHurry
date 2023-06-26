package com.ant.hurry.boundedContext.coin.service;

import com.ant.hurry.boundedContext.coin.entity.CoinLog;
import com.ant.hurry.boundedContext.coin.repository.CoinLogRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinLogRepository coinLogRepository;

    @Transactional
    public CoinLog addCoin(Member member, long price, String eventType) {
        CoinLog coinLog = CoinLog.builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();
        return coinLogRepository.save(coinLog);
    }



}
