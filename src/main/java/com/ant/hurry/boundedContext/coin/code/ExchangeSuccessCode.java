package com.ant.hurry.boundedContext.coin.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExchangeSuccessCode implements Code {

    COIN_ENOUGH("S_E-1", "충분한 코인을 가지고있습니다."),
    SUCCESS_APPLY_EXCHANGE("S_E-2", "환전신청 되었습니다."),
    EDIT_APPLY_EXCHANGE("S_E-3", "수정되었습니다."),
    CANCEL_APPLY_EXCHANGE("S_E-4", "취소되었습니다."),
    SUCCESS_DELETE_APPLY_EXCHANGE("S_E-5", "삭제되었습니다.");

    public String code;
    public String message;

}
