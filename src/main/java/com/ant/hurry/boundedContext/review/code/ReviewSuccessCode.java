package com.ant.hurry.boundedContext.review.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewSuccessCode implements Code {

    CREATE_SUCCESS("S_R-1", "후기가 성공적으로 등록되었습니다."),
    REDIRECT_TO_CREATE_REVIEW_PAGE("S_R-2", "후기등록페이지로 이동합니다."),
    REDIRECT_TO_REVIEW_LIST_PAGE("S_R-3", "후기목록페이지로 이동합니다.");

    private String code;
    private String message;
}
