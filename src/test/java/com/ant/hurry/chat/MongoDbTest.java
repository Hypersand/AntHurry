package com.ant.hurry.chat;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test-mongo.yml, classpath:application.yml"})
@AutoConfigureTestDatabase
public class MongoDbTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @AfterEach
    void rollback() {
        chatRoomRepository.deleteAll();
    }

    @Test
    public void insertByRepository() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        assertThat(chatRoom).isEqualTo(insertChatRoom);
    }

    @Test
    public void insertByMongoTemplate() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = reactiveMongoTemplate.insert(chatRoom).block();

        assertThat(insertChatRoom.getId()).isNotNull();
    }

    @Test
    public void update() {
        ChatRoom chatRoom = ChatRoom.builder().build();

        TradeStatus tradeStatus = TradeStatus.builder().build();
        ChatRoom updateChatRoom = chatRoom.toBuilder().tradeStatus(tradeStatus).build();
        updateChatRoom = chatRoomRepository.insert(updateChatRoom).block();

        assertThat(updateChatRoom.getTradeStatus()).isNotNull();
    }

    @Test
    public void findAndDelete() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        Optional<ChatRoom> foundChatRoom = chatRoomRepository.findById(insertChatRoom.getId()).blockOptional();
        assertThat(foundChatRoom).isPresent();

        chatRoomRepository.delete(foundChatRoom.get()).block();
        boolean exists = chatRoomRepository.existsById(insertChatRoom.getId()).block();
        assertThat(exists).isFalse();
    }

}
