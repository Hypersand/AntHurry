package com.ant.hurry.boundedContext.adm.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.coin.entity.Exchange;
import com.ant.hurry.boundedContext.coin.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.ant.hurry.boundedContext.adm.code.AdmErrorCode.APPLY_NO_EXISTS;
import static com.ant.hurry.boundedContext.adm.code.AdmSuccessCode.ACCEPT_APPLY;

@Service
@RequiredArgsConstructor
public class AdmService {

    private final ExchangeRepository exchangeRepository;

    @Transactional(readOnly = true)
    public List<Exchange> getApplyList(){
        return exchangeRepository.findByStatusFalse();
    }

    @Transactional
    public RsData accept(Long id) {
        Exchange apply = exchangeRepository.findById(id).orElse(null);
        if(ObjectUtils.isEmpty(apply)){
            return RsData.of(APPLY_NO_EXISTS);
        }
        apply.setStatus(true);
        return RsData.of(ACCEPT_APPLY);
    }
}
