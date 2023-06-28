package com.ant.hurry.chat.entity;

import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
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

    private LocalDateTime deletedAt;

    private LocalDateTime readAt;

    private String content;

    private ChatRoom chatRoom;

    private Member sender;

    public String getChatRoomId() {
        return chatRoom.getId();
    }

    public void markAsRead() {
        readAt = LocalDateTime.now();
    }

    public boolean isNotRead() {
        return readAt == null;
    }
}
