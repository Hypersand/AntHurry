package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.LatestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomLatestMessageRepositoryImpl implements CustomLatestMessageRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<LatestMessage> findByChatRoomId(String roomId) {
        Query query = new Query(Criteria.where("roomId").is(roomId));
        return Optional.ofNullable(mongoTemplate.findOne(query, LatestMessage.class));
    }

}
