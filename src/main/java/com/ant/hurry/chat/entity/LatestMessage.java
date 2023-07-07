package com.ant.hurry.chat.entity;

import com.ant.hurry.chat.baseEntity.BaseMessage;
import com.ant.hurry.chat.baseEntity.Message;
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

    private String writer;

    @Builder.Default
    private Message message = null;

}
