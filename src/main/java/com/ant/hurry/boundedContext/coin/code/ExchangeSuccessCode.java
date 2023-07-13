package com.ant.hurry.boundedContext.coin.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExchangeSuccessCode implements Code {

    COIN_ENOUGH("S_E-1", "충분한 코인을 가지고 있습니다."),
    SUCCESS_APPLY_EXCHANGE("S_E-2", "환전 신청 되었습니다."),
    EDIT_APPLY_EXCHANGE("S_E-3", "수정되었습니다."),
    CANCEL_APPLY_EXCHANGE("S_E-4", "취소되었습니다."),
    SUCCESS_DELETE_APPLY_EXCHANGE("S_E-5", "삭제되었습니다."),
    CAN_DELETE_APPLY_EXCHANGE("S_E-6", "취소가 가능합니다."),
    SUCCESS_CHARGE("S_E-7", "충전이 완료되었습니다.");

    private String code;
    private String message;

}
