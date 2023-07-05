package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>, CustomChatMessageRepository {
}
