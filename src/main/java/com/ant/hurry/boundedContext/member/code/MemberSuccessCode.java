package com.ant.hurry.boundedContext.member.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberSuccessCode implements Code {

    SUCCESS_LOGIN("S_M-1", "로그인 되었습니다."),
    SUCCESS_SIGNUP("S_M-2", "회원가입이 완료되었습니다."),

    CERTIFICATION_PHONE_NUMBER("S_M-3", "전화번호 인증이 완료되었습니다.");

    private String code;
    private String message;
}
