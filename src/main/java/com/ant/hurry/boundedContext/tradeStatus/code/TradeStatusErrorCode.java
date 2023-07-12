package com.ant.hurry.boundedContext.tradeStatus.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeStatusErrorCode implements Code {

    ALREADY_IN_PROGRESS("F_T-1", "진행 이력이 있는 거래입니다."),
    COMPLETE_FAILED("F_T-2", "현재 진행 중인 거래만 완료할 수 있습니다."),
    ALREADY_COMPLETED("F_T-3", "이미 완료된 거래입니다."),
    TRADESTATUS_NOT_EXISTS("F_T-4", "존재하지 않는 거래입니다."),
    UNAUTHORIZED("F_T-5", "거래 상태를 변경할 권한이 없습니다."),
    CANNOT_WRITE_REVIEW("F_T-6", "아직 리뷰를 남길 수 없는 거래입니다.");


    private String code;
    private String message;
}
