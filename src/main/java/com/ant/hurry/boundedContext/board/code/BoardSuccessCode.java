package com.ant.hurry.boundedContext.board.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardSuccessCode implements Code {

    CREATE_BOARD("S_B-1", "게시글이 작성되었습니다."),
    CAN_REMOVE_BOARD("S_B-2", "삭제 가능합니다."),
    REMOVE_BOARD("S_B-3", "게시글이 삭제되었습니다."),
    CAN_EDIT_BOARD("S_B-3", "수정 가능합니다."),
    EDIT_BOARD("S_B-3", "게시글이 수정되었습니다.");

    private String code;
    private String message;
}
