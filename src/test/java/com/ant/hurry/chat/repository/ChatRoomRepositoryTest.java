package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void refresh() {
        chatRoomRepository.deleteAll();
    }

    @Test
    @DisplayName("repository를 통해 채팅방을 저장합니다.")
    void chatRoom_saveByRepository() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        chatRoomRepository.insert(chatRoom);

        assertThat(chatRoomRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("MongoTemplate을 통해 채팅방을 저장합니다.")
    void chatRoom_saveByMongoTemplate() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        mongoTemplate.insert(chatRoom);

        assertThat(mongoTemplate.findAll(ChatRoom.class)).hasSize(1);
    }

    @Test
    @DisplayName("채팅방을 저장하고 저장된 채팅방을 수정합니다.(tradeStatus의 값을 추가)")
    void chatRoom_save_update() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom);

        TradeStatus tradeStatus = TradeStatus.builder().build();
        ChatRoom updateChatRoom = insertChatRoom.toBuilder().tradeStatus(tradeStatus).build();
        chatRoomRepository.save(updateChatRoom);

        Optional<ChatRoom> updatedChatRoom = chatRoomRepository.findById(updateChatRoom.getId());
        assertThat(updatedChatRoom).isPresent();
        assertThat(updatedChatRoom.get().getTradeStatus()).isNotNull();
    }

    @Test
    @DisplayName("채팅방을 저장한 후 조회하고, 삭제합니다.")
    void chatRoom_save_find_delete() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom);

        Optional<ChatRoom> foundChatRoom = chatRoomRepository.findById(insertChatRoom.getId());
        assertThat(foundChatRoom).isPresent();

        chatRoomRepository.delete(foundChatRoom.get());
        assertThat(chatRoomRepository.existsById(insertChatRoom.getId())).isFalse();
    }

    @Test
    @DisplayName("List<TradeStatus>로 채팅방을 조회합니다.")
    void chatRoom_findByTradeStatus() {
        TradeStatus tradeStatus = TradeStatus.builder().id(1L).build();
        List<TradeStatus> list = List.of(tradeStatus);

        ChatRoom chatRoom1 = ChatRoom.builder().tradeStatus(tradeStatus).build();
        ChatRoom chatRoom2 = ChatRoom.builder().build();

        chatRoomRepository.insert(chatRoom1);
        chatRoomRepository.insert(chatRoom2);

        assertThat(chatRoomRepository.findByTradeStatus(list)).hasSize(1);
    }

}
