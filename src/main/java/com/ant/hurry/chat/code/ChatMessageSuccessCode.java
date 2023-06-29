package com.ant.hurry.chat.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageSuccessCode implements Code {

    MESSAGE_FOUND("S_G-1", "메시지를 조회했습니다."),
    MESSAGE_SENT("S_G-2", "메시지를 보냈습니다."),
    MESSAGE_DELETED("S_G-3", "메시지를 삭제했습니다."),
    FILE_SAVED("S_G-4", "파일이 저장되었습니다."),
    FILE_DOWNLOADED("S_G-5", "파일을 저장했습니다.");


    public String code;
    public String message;

}
