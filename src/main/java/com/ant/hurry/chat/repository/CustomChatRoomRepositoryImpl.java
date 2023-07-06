package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;

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
    public Optional<ChatRoom> findByTradeStatusId(Long id) {
        Criteria criteria = Criteria.where("tradeStatus.id").is(id);
        Query query = Query.query(criteria);
        ChatRoom chatRoom = mongoTemplate.findOne(query, ChatRoom.class);
        return Optional.ofNullable(chatRoom);
    }

    @Override
    public void updateStatusOfChatRoom(ChatRoom chatRoom, String status) {
        Criteria criteria = Criteria.where("_id").is(chatRoom.getId());
        Query query = Query.query(criteria);
        Update update = Update.update("tradeStatus.status", status);
        mongoTemplate.updateFirst(query, update, ChatRoom.class);
    }

    @Override
    public void deleteMembers(ChatRoom chatRoom, Member member) {
        Criteria criteria = Criteria.where("_id").is(chatRoom.getId());
        Query query = Query.query(criteria);
        Update update = new Update().pull("members", member);
        mongoTemplate.updateFirst(query, update, ChatRoom.class);
    }

}
