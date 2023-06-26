package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
}
