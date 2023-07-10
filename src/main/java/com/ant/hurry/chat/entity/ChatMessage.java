package com.ant.hurry.chat.entity;

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

    private String writer;

    private LocalDateTime deletedAt;

    @Override
    public boolean isFileMessage() {
        return false;
    }
}
