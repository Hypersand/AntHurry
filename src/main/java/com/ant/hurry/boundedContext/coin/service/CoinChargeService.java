package com.ant.hurry.boundedContext.coin.service;

import com.ant.hurry.boundedContext.coin.entity.CoinChargeLog;
import com.ant.hurry.boundedContext.coin.repository.CoinChargeLogRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoinChargeService {
    private final CoinChargeLogRepository coinChargeLogRepository;

    @Transactional
    public CoinChargeLog addCoin(Member member, long price, String eventType) {
        CoinChargeLog coinChargeLog = CoinChargeLog.builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();
        return coinChargeLogRepository.save(coinChargeLog);
    }



}
