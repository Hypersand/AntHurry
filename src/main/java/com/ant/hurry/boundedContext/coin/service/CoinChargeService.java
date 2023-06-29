package com.ant.hurry.boundedContext.coin.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.coin.dto.ExchangeRequest;
import com.ant.hurry.boundedContext.coin.entity.CoinChargeLog;
import com.ant.hurry.boundedContext.coin.entity.Exchange;
import com.ant.hurry.boundedContext.coin.repository.CoinChargeLogRepository;
import com.ant.hurry.boundedContext.coin.repository.ExchangeRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class CoinChargeService {
    private final CoinChargeLogRepository coinChargeLogRepository;
    private final Rq rq;
    private final ExchangeRepository exchangeRepository;

    @Transactional
    public CoinChargeLog addCoin(Member member, long price, String eventType) {
        CoinChargeLog coinChargeLog = CoinChargeLog.builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();
        return coinChargeLogRepository.save(coinChargeLog);
    }

    @Transactional
    public void applyExchange(ExchangeRequest exchangeRequest) {
        Member member = rq.getMember();
        member.decreaseCoin((int) exchangeRequest.getMoney());
        Exchange exchange = Exchange.builder()
                .member(member)
                .status(false)
                .accountNumber(exchangeRequest.getAccountNumber())
                .bankType(exchangeRequest.getBankType())
                .holderName(exchangeRequest.getHolderName())
                .money(exchangeRequest.getMoney())
                .build();
        exchangeRepository.save(exchange);

        CoinChargeLog coinChargeLog = CoinChargeLog.builder()
                .member(member)
                .price(exchangeRequest.getMoney())
                .eventType("환전")
                .build();
        coinChargeLogRepository.save(coinChargeLog);
    }
}
