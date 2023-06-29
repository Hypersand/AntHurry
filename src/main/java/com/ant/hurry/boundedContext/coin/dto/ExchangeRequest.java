package com.ant.hurry.boundedContext.coin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExchangeRequest {
    private String bank;
    private String accountNumber;
    private String holderName;
    private String money;
}
