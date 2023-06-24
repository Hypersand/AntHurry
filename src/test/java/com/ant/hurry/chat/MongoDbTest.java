package com.ant.hurry.chat;

import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureDataMongo
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
    public void insertDataWithRepository() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.save(chatRoom);

        assertThat(chatRoom).isEqualTo(insertChatRoom);
    }

    @Test
    public void insertDataWithMongoTemplate() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = reactiveMongoTemplate.insert(chatRoom).block();

        assertThat(chatRoom.getId()).isEqualTo(insertChatRoom.getId());
    }

}
