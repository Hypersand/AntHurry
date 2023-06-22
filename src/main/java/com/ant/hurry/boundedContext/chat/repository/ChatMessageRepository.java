package com.ant.hurry.boundedContext.chat.repository;

import com.ant.hurry.boundedContext.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
