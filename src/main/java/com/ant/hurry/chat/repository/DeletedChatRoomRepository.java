package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.DeletedChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedChatRoomRepository extends MongoRepository<DeletedChatRoom, String> {
}
