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
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.ant.hurry.boundedContext.adm.code.AdmErrorCode.APPLY_NOT_EXISTS;
import static com.ant.hurry.boundedContext.coin.code.ExchangeSuccessCode.CAN_EDIT_APPLY_EXCHANGE;

@Service
@RequiredArgsConstructor

public class CoinService {
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
    }

    public List<Exchange> getExchangeList(Member member) {
        return exchangeRepository.findByMember(member);
    }

    @Transactional
    public RsData modifyApplyExchange(ExchangeRequest exchangeRequest, Long exchangeId) {
        Exchange exchange = exchangeRepository.findByIdWithMember(exchangeId).orElse(null);
        if(ObjectUtils.isEmpty(exchange)){
            return RsData.of(APPLY_NOT_EXISTS);
        }
        int difference = exchange.getMoney() - exchangeRequest.getMoney();
        exchange.getMember().increaseCoin(difference);
        exchange.update(exchangeRequest);
        return RsData.of(CAN_EDIT_APPLY_EXCHANGE);
    }
}
