package com.ant.hurry.chat.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@SuperBuilder
@Document(collection = "chat_image")
public class ChatMessageImage {

    @Id
    private String id;

    private String uploadFileName;

    private String storedFileName;

    private String fullPath;

    private ChatMessage chatMessage;

}
