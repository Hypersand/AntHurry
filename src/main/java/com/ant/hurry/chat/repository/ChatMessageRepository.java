package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>, CustomChatMessageRepository {
}
