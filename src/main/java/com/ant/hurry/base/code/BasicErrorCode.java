package com.ant.hurry.base.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BasicErrorCode implements Code {

    UNAUTHORIZED("F_A-1", "접근 권한이 없습니다.");

    public String code;
    public String message;

}
