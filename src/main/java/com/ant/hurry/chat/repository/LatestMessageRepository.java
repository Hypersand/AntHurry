package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.LatestMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LatestMessageRepository extends MongoRepository<LatestMessage, String>, CustomLatestMessageRepository {
}
