package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatFileMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatFileMessageRepository extends MongoRepository<ChatFileMessage, String> {
}
