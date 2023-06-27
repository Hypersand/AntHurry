package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.service.ChatRoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class ChatMessageRepositoryTest {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    ChatMessageRepository chatMessageRepository;

    @AfterEach
    void refresh() {
        chatMessageRepository.deleteAll().block();
    }

    @Test
    @DisplayName("repository를 통해 채팅 메시지를 생성하고 저장합니다.")
    void saveByRepository() {
        ChatRoom chatRoom = chatRoomService.create(TradeStatus.builder().build()).getData();
        ChatMessage chatMessage = ChatMessage.builder().chatRoom(chatRoom).build();
        chatMessageRepository.save(chatMessage).block();

        assertThat(chatMessageRepository.findAll().collectList().block())
                .hasSize(1);
    }

    @Test
    @DisplayName("ReactiveMongoTemplate을 통해 채팅 메시지를 생성하고 저장합니다.")
    void saveByMongoTemplate() {
        ChatRoom chatRoom = chatRoomService.create(TradeStatus.builder().build()).getData();
        ChatMessage chatMessage = ChatMessage.builder().chatRoom(chatRoom).build();
        reactiveMongoTemplate.save(chatMessage).block();

        assertThat(reactiveMongoTemplate.findAll(ChatMessage.class).collectList().block())
                .hasSize(1);
    }

    @Test
    @DisplayName("채팅 메시지를 저장한 후 조회하고, 삭제합니다.")
    void save_find_delete() {
        ChatRoom chatRoom = chatRoomService.create(TradeStatus.builder().build()).getData();
        ChatMessage chatMessage = ChatMessage.builder().chatRoom(chatRoom).build();
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage).block();

        assertThat(chatMessageRepository.findById(chatMessage.getId())).isNotNull();

        chatMessageRepository.delete(savedChatMessage).block();
        assertThat(chatMessageRepository.findAll().collectList().block()).hasSize(0);
    }

}
