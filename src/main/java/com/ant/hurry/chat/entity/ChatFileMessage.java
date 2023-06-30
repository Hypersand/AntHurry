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
@Document(collection = "chat_file")
public class ChatFileMessage {

    @Id
    private String id;

    private String uploadFilePath;

    private String uploadFileId;

    private ChatRoom chatRoom;

    private Member sender;

    @CreatedDate
    private LocalDateTime createdAt;

}
