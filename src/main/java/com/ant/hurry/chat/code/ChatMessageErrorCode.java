package com.ant.hurry.chat.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageErrorCode implements Code {

    MESSAGE_NOT_DELETED("F_R-1", "메시지 삭제에 실패했습니다.");

    public String code;
    public String message;

}
