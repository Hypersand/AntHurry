package com.ant.hurry.boundedContext.coin.code;


import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExchangeErrorCode implements Code {

    COIN_NOT_ENOUGH("F_E-1", "충분한 돈을 가지고 있지 않습니다."),
    NOT_EXCHANGE("F_E-2", "0원을 환전할 수 없습니다.");

    public String code;
    public String message;
}
