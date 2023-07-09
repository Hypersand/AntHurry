package com.ant.hurry.chat.entity;

import com.ant.hurry.chat.baseEntity.BaseMessage;
import com.ant.hurry.chat.baseEntity.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Document(collection = "latest_message")
public class LatestMessage extends BaseMessage {

    private String writer;

    @Builder.Default
    private LocalDateTime readAt = null;

    @Builder.Default
    private Message message = null;

    public LatestMessage markAsRead() {
        this.readAt = LocalDateTime.now();
        return this;
    }

    public boolean isNotRead() {
        return this.readAt == null;
    }

}
