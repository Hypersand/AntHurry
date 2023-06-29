package com.ant.hurry.chat.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRoomSuccessCode implements Code {

    CHATROOM_FOUND("S_C-1", "채팅방을 조회했습니다."),
    CHATROOM_CREATED("S_C-2", "채팅방이 생성되었습니다."),
    CHATROOM_DELETED("S_C-3", "채팅방이 삭제되었습니다."),
    CHATROOM_EXITED("S_C-4", "채팅방에서 퇴장했습니다.");

    public String code;
    public String message;

}
