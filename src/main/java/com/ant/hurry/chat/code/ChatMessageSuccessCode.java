package com.ant.hurry.chat.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageSuccessCode implements Code {

    MESSAGE_SENT("S_M-1", "메시지를 보냈습니다."),
    MESSAGE_DELETED("S_M-1", "메시지를 삭제했습니다.");

    public String code;
    public String message;

}
