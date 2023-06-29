package com.ant.hurry.boundedContext.adm.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdmErrorCode implements Code {
    APPLY_NOT_EXISTS("F_A-1", "존재하지 않는 환전요청입니다.");

    public String code;
    public String message;
}
