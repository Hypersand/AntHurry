package com.ant.hurry.chat.event;

import com.ant.hurry.chat.entity.ChatRoom;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterDeletedChatRoom extends ApplicationEvent {

    private final ChatRoom chatRoom;

    public EventAfterDeletedChatRoom(Object source, ChatRoom chatRoom) {
        super(source);
        this.chatRoom = chatRoom;
    }
}
