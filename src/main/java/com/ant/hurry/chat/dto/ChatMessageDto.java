package com.ant.hurry.chat.dto;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    private String content;

    private ChatRoom chatRoom;

    private Member sender;

}
