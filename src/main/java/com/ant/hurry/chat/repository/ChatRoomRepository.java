package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String>, CustomChatRoomRepository {
}
