package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomChatMessageRepositoryImpl implements CustomChatMessageRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public ChatMessage deleteSoft(ChatMessage chatMessage) {
        Query query = new Query(Criteria.where("_id").is(chatMessage.getId()));
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        mongoTemplate.findAndModify(query, update, ChatMessage.class);
        return mongoTemplate.findOne(query, ChatMessage.class);
    }

    @Override
    public List<ChatMessage> findByChatRoomId(String roomId) {
        Criteria criteria = Criteria.where("roomId").is(roomId);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, ChatMessage.class);
    }
}
