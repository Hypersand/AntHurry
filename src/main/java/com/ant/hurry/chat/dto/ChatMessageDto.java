package com.ant.hurry.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {

    private String roomId;

    private String message;

    private String writer;

}
