package com.ant.hurry.chat.entity;

import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@Document(collection = "chat_message")
public class ChatMessage {

    @Id
    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String content;

    private ChatRoom chatRoom;

    private Member sender;

}
