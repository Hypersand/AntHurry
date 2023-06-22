package com.ant.hurry.boundedContext.tradeStatus.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Status {

    BEFORE("거래 전"),
    INPROGRESS("거래 중"),
    COMPLETE("거래 완료"),
    CANCLED("거래 취소");

    public String msg;

}
