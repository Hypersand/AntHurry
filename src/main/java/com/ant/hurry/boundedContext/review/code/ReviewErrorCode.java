package com.ant.hurry.boundedContext.review.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements Code {

    ALREADY_WRITE_REVIEW("F_R-1", "이미 후기를 작성했습니다.");

    private String code;
    private String message;
}
