package com.ant.hurry.boundedContext.tradeStatus.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeStatusErrorCode implements Code {

    ALREADY_INPROGRESS("F_T-1", "진행 이력이 있는 거래입니다."),
    COMLETE_FAILED("F_T-2", "현재 진행 중인 거래만 완료할 수 있습니다."),
    ALREADY_COMPLETED("F_T-3", "이미 완료된 거래입니다.");


    public String code;
    public String message;
}
