package com.ant.hurry.boundedContext.adm.service;

import com.ant.hurry.boundedContext.coin.entity.Exchange;
import com.ant.hurry.boundedContext.coin.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmService {

    private final ExchangeRepository exchangeRepository;

    public List<Exchange> getApplyList(){
        return exchangeRepository.findByStatusFalse();
    }
}
