package com.ant.hurry.boundedContext.coin.code;


import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExchangeErrorCode implements Code {

    COIN_NOT_ENOUGH("F_E-1", "충분한 코인을 가지고 있지 않습니다."),
    CANNOT_EXCHANGE("F_E-2", "0원을 환전할 수 없습니다."),
    NOT_EXISTS_APPLY_EXCHANGE("F_E-3", "존재하지 않는 환전 신청입니다."),
    NOT_MATCH_MEMBER("F_E-4", "로그인한 회원과 충전할 회원이 일치하지 않습니다.");

    private String code;
    private String message;
}
