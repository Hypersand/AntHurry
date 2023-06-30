package com.ant.hurry.chat.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Document(collection = "latest_message")
public class LatestMessage extends BaseMessage {

    @Builder.Default
    private Message message = null;

}
