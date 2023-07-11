package com.ant.hurry.boundedContext.adm.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdmErrorCode implements Code {
    APPLY_NOT_EXISTS("F_A-1", "존재하지 않는 환전 요청입니다.");

    private String code;
    private String message;
}
