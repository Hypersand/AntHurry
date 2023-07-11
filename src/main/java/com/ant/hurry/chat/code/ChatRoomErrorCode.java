package com.ant.hurry.chat.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements Code {

    CHATROOM_NO_EXISTS("F_C-1", "존재하지 않는 채팅방입니다."),
    CHATROOM_NOT_DELETED("F_C-2", "채팅방 삭제에 실패했습니다."),
    CHATROOM_ALREADY_EXITED("F_C-3", "이미 퇴장한 채팅방입니다.");

    private String code;
    private String message;

}
