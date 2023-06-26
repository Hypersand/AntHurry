package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class ChatRoomServiceTest {

    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @AfterEach
    void refresh() {
        chatRoomRepository.deleteAll().block();
    }

    @Test
    void create_findById() {
        TradeStatus tradeStatus = TradeStatus.builder().build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        RsData<ChatRoom> findRs = chatRoomService.findById(createdRs.getData().getId());

        assertThat(findRs.getData()).isNotNull();
    }

    @Test
    void create_softDelete() {
        TradeStatus tradeStatus = TradeStatus.builder().id(1L).build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        ChatRoom createdChatRoom = createdRs.getData();
        assertThat(chatRoomService.findAll().getData().size()).isEqualTo(1);

        chatRoomService.deleteSoftly(createdChatRoom);
        List<ChatRoom> chatRooms = chatRoomService.findAll().getData();
        assertThat(chatRooms.stream()
                .filter(cr -> cr.getDeletedAt() != null).count())
                .isEqualTo(1);
    }

    @Test
    void create_hardDelete() {
        TradeStatus tradeStatus = TradeStatus.builder().id(1L).build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        ChatRoom createdChatRoom = createdRs.getData();
        assertThat(chatRoomService.findAll().getData().size()).isEqualTo(1);

        chatRoomService.delete(createdChatRoom);
        assertThat(chatRoomService.findAll().getData().size()).isEqualTo(1);
    }

}
