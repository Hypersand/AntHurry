package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;

import java.util.List;

public interface CustomChatMessageRepository {
    ChatMessage deleteSoft(ChatMessage chatMessage);
    List<ChatMessage> findByChatRoomId(String roomId);
}
