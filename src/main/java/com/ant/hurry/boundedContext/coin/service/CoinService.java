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
import static com.ant.hurry.boundedContext.coin.code.ExchangeSuccessCode.*;

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
    public RsData applyExchange(ExchangeRequest exchangeRequest) {
        Member member = rq.getMember();
        member.decreaseCoin(exchangeRequest.getMoney());
        Exchange exchange = Exchange.builder()
                .member(member)
                .status(false)
                .accountNumber(exchangeRequest.getAccountNumber())
                .bankType(exchangeRequest.getBankType())
                .holderName(exchangeRequest.getHolderName())
                .money(exchangeRequest.getMoney())
                .build();
        exchangeRepository.save(exchange);
        return RsData.of(SUCCESS_APPLY_EXCHANGE);
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
        return RsData.of(EDIT_APPLY_EXCHANGE);
    }

    public RsData canCancelExchange(Long exchangeId) {
        Exchange exchange = exchangeRepository.findByIdWithMember(exchangeId).orElse(null);
        if(ObjectUtils.isEmpty(exchange)){
            return RsData.of(APPLY_NOT_EXISTS);
        }
        return RsData.of(NOT_EXISTS_APPLY_EXCHANGE, exchange);
    }

    @Transactional
    public RsData cancelExchange(Exchange exchange) {
        exchange.getMember().increaseCoin(exchange.getMoney());
        exchangeRepository.delete(exchange);
        return RsData.of(CANCEL_APPLY_EXCHANGE);
    }

    @Transactional
    public RsData deleteExchangeInfo(Exchange exchange) {
        exchangeRepository.delete(exchange);
        return RsData.of(SUCCESS_DELETE_APPLY_EXCHANGE);
    }
}
