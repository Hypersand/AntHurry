package com.ant.hurry.boundedContext.coin.dto;

import com.ant.hurry.boundedContext.coin.entity.BankType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExchangeRequest {
    private BankType bankType;
    private String accountNumber;
    private String holderName;
    private long money;
}
