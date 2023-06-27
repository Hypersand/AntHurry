package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CustomChatMessageRepositoryImpl implements CustomChatMessageRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<ChatMessage> deleteSoft(ChatMessage chatMessage) {
        Query query = new Query(Criteria.where("_id").is(chatMessage.getId()));
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update, ChatMessage.class);
    }
}
