package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @AfterEach
    void refresh() {
        chatRoomRepository.deleteAll().block();
    }

    @Test
    void insertByRepository() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        assertThat(chatRoom).isEqualTo(insertChatRoom);
    }

    @Test
    void insertByMongoTemplate() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = reactiveMongoTemplate.insert(chatRoom).block();

        assertThat(insertChatRoom.getId()).isNotNull();
    }

    @Test
    void update() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        TradeStatus tradeStatus = TradeStatus.builder().build();
        ChatRoom updateChatRoom = insertChatRoom.toBuilder().tradeStatus(tradeStatus).build();
        chatRoomRepository.save(updateChatRoom).block();

        ChatRoom updatedChatRoom = chatRoomRepository.findById(updateChatRoom.getId()).block();
        assertThat(updatedChatRoom.getTradeStatus()).isNotNull();
    }

    @Test
    void findAndDelete() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        Optional<ChatRoom> foundChatRoom = chatRoomRepository.findById(insertChatRoom.getId()).blockOptional();
        assertThat(foundChatRoom).isPresent();

        chatRoomRepository.delete(foundChatRoom.get()).block();
        boolean isExists = chatRoomRepository.existsById(insertChatRoom.getId()).block();
        assertThat(isExists).isFalse();
    }

}
