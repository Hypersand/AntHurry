package com.ant.hurry.boundedContext.member.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements Code {

    MEMBER_NOT_EXISTS("F_M-1", "존재하지 않는 회원입니다."),
    CANNOT_ACCESS("F_M-2", "접근할 수 있는 권한이 없습니다."),
    NOT_INPUT_PHONE_NUMBER("F_M-3", "전화번호를 입력해서 인증번호를 받아주세요."),
    NOT_CERTIFICATION_NUMBER("F_M-4", "인증번호 검증이 완료되지 않았습니다."),
    NOT_MATCH_NUMBER("F_M-5", "입력하신 전화번호와 인증번호를 받은 번호가 일치하지 않습니다.");

    private String code;
    private String message;
}
