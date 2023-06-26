package com.ant.hurry.chat.entity;

import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder
@Document(collection = "chat_message")
public class ChatMessage {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String content;

    private ChatRoom chatRoom;

    private Member sender;

}
