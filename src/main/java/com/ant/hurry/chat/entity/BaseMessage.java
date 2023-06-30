package com.ant.hurry.chat.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseMessage {

    @Id
    private String id;

    private ChatRoom chatRoom;

    @CreatedDate
    private LocalDateTime createdAt;

}
