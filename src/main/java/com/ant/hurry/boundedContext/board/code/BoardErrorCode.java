package com.ant.hurry.boundedContext.board.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements Code {

    NOT_EXISTS_BOARD("F_B-1", "존재하지 않는 게시물입니다."),
    CAN_NOT_REMOVE_BOARD("F_B-2", "삭제할 권한이 없습니다."),
    CAN_NOT_EDIT_BOARD("F_B-3", "수정할 권한이 없습니다.");

    private String code;
    private String message;
}
