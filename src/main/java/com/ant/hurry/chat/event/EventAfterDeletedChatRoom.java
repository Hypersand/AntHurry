package com.ant.hurry.chat.event;

import com.ant.hurry.chat.entity.ChatRoom;
import lombok.Getter;

@Getter
public class EventAfterDeletedChatRoom {

    private final ChatRoom chatRoom;

    public EventAfterDeletedChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
