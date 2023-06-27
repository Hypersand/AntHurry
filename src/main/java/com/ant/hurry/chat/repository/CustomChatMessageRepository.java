package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;
import reactor.core.publisher.Mono;

public interface CustomChatMessageRepository {
    Mono<ChatMessage> deleteSoft(ChatMessage chatMessage);
}
