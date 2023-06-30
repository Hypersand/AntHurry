package com.ant.hurry.boundedContext.coin.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExchangeSuccessCode implements Code {

    COIN_ENOUGH("S_E-1", "충분한 코인을 가지고있습니다."),
    CAN_EDIT_APPLY_EXCHANGE("S_E-2", "성공적으로 수정되었습니다.");

    public String code;
    public String message;

}
