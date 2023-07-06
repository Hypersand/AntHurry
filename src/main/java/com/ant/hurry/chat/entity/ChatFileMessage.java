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
@Document(collection = "chat_file")
public class ChatFileMessage extends BaseMessage implements Message {

    private String uploadFilePath;

    private String uploadFileId;

    private String sender;

    private LocalDateTime deletedAt;

    private LocalDateTime readAt;

    public void markAsRead() {
        readAt = LocalDateTime.now();
    }

    public boolean isNotRead() {
        return readAt == null;
    }

    @Override
    public String getMessage() {
        return "파일을 다운로드할 수 있습니다.";
    }
}
