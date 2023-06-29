package com.ant.hurry.boundedContext.adm.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdmSuccessCode implements Code {
    ACCEPT_APPLY("S_A-1", "성공적으로 처리되었습니다.");

    public String code;
    public String message;
}
