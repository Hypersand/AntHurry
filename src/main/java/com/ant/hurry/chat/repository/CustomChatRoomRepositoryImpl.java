package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomChatRoomRepositoryImpl implements CustomChatRoomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatRoom> findByTradeStatus(List<TradeStatus> tradeStatuses) {
        Criteria criteria = Criteria.where("tradeStatus").in(tradeStatuses);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, ChatRoom.class);
    }

    @Override
    public List<ChatRoom> findAllAndDeletedAtIsNull() {
        Criteria criteria = Criteria.where("deletedAt").isNull();
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, ChatRoom.class);
    }

    @Override
    public ChatRoom deleteSoft(ChatRoom chatRoom) {
        Query query = new Query(Criteria.where("_id").is(chatRoom.getId()));
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update, ChatRoom.class);
    }

}
