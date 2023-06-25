package com.ant.hurry.chat.entity;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Document(collection = "chat_room")
public class ChatRoom {

    @Id
    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private TradeStatus tradeStatus;

}
