package com.ant.hurry.chat.entity;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.baseEntity.BaseMessage;
import com.ant.hurry.chat.baseEntity.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Document(collection = "chat_message")
public class ChatMessage extends BaseMessage implements Message {

    private String message;

    private Member writer;

    private LocalDateTime deletedAt;

    private LocalDateTime readAt;

    public void markAsRead() {
        readAt = LocalDateTime.now();
    }

    public boolean isNotRead() {
        return readAt == null;
    }
}
