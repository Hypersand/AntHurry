package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;

public interface CustomChatMessageRepository {
    ChatMessage deleteSoft(ChatMessage chatMessage);
}
