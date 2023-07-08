package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatFileMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatFileMessageRepository extends MongoRepository<ChatFileMessage, String> {
    Optional<ChatFileMessage> findByUploadFileId(String fileId);
    List<ChatFileMessage> findByRoomId(String roomId);
}
