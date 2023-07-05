package com.ant.hurry.chat.baseEntity;

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

    private String roomId;

    @CreatedDate
    private LocalDateTime createdAt;

}
