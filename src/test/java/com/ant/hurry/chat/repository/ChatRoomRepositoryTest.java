package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
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
    @DisplayName("repository를 통해 채팅방을 저장합니다.")
    void saveByRepository() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        chatRoomRepository.insert(chatRoom).block();

        assertThat(chatRoomRepository.findAll().collectList().block())
                .hasSize(1);
    }

    @Test
    @DisplayName("ReactiveMongoTemplate을 통해 채팅방을 저장합니다.")
    void saveByMongoTemplate() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        reactiveMongoTemplate.insert(chatRoom).block();

        assertThat(reactiveMongoTemplate.findAll(ChatRoom.class).collectList().block())
                .hasSize(1);
    }

    @Test
    @DisplayName("채팅방을 저장하고 저장된 채팅방을 수정합니다.(tradeStatus의 값을 추가)")
    void save_update() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        TradeStatus tradeStatus = TradeStatus.builder().build();
        ChatRoom updateChatRoom = insertChatRoom.toBuilder().tradeStatus(tradeStatus).build();
        chatRoomRepository.save(updateChatRoom).block();

        ChatRoom updatedChatRoom = chatRoomRepository.findById(updateChatRoom.getId()).block();
        assertThat(updatedChatRoom.getTradeStatus()).isNotNull();
    }

    @Test
    @DisplayName("채팅방을 저장한 후 조회하고, 삭제합니다.")
    void save_find_delete() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        Optional<ChatRoom> foundChatRoom = chatRoomRepository.findById(insertChatRoom.getId()).blockOptional();
        assertThat(foundChatRoom).isPresent();

        chatRoomRepository.delete(foundChatRoom.get()).block();
        assertThat(chatRoomRepository.existsById(insertChatRoom.getId()).block()).isFalse();
    }

    @Test
    @DisplayName("List<TradeStatus>로 채팅방을 조회합니다.")
    void findByTradeStatus() {
        TradeStatus tradeStatus = TradeStatus.builder().id(1L).build();
        List<TradeStatus> list = List.of(tradeStatus);

        ChatRoom chatRoom1 = ChatRoom.builder().tradeStatus(tradeStatus).build();
        ChatRoom chatRoom2 = ChatRoom.builder().build();

        chatRoomRepository.insert(chatRoom1).block();
        chatRoomRepository.insert(chatRoom2).block();

        assertThat(chatRoomRepository.findByTradeStatus(list).collectList().block()).hasSize(1);
    }

    @Test
    @DisplayName("deletedAt 필드가 null인 채팅방만 조회합니다.")
    void findAllAndDeletedAtIsNull() {
        ChatRoom chatRoom1 = ChatRoom.builder().build();
        ChatRoom chatRoom2 = ChatRoom.builder().deletedAt(LocalDateTime.now()).build();

        chatRoomRepository.insert(chatRoom1).block();
        chatRoomRepository.insert(chatRoom2).block();

        assertThat(chatRoomRepository.findAllAndDeletedAtIsNull().collectList().block()).hasSize(1);
    }

    @Test
    @DisplayName("채팅방을 저장하고 저장된 채팅방을 soft-delete합니다.(deletedAt 필드 값 생성)")
    void save_deleteSoft() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom).block();

        chatRoomRepository.deleteSoft(insertChatRoom).block();
        assertThat(chatRoomRepository.findAll().blockFirst().getDeletedAt()).isNotNull();
    }

}
