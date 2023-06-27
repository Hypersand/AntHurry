package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("채팅방을 생성하고 생성된 채팅방을 조회합니다.")
    void create_findById() {
        TradeStatus tradeStatus = TradeStatus.builder().build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        RsData<ChatRoom> findRs = chatRoomService.findById(createdRs.getData().getId());

        assertThat(findRs.getData()).isNotNull();
    }

    @Test
    @DisplayName("채팅방을 생성하고 생성된 채팅방을 soft-delete합니다.(deletedAt 필드 값 생성")
    void create_deleteSoft() {
        TradeStatus tradeStatus = TradeStatus.builder().id(1L).build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        ChatRoom createdChatRoom = createdRs.getData();
        assertThat(chatRoomService.findAll().getData()).hasSize(1);

        chatRoomService.deleteSoft(createdChatRoom);
        assertThat(chatRoomService.findAll().getData()).hasSize(1);
        assertThat(chatRoomService.findAllAndDeletedAtIsNull().getData()).isEmpty();
    }

    @Test
    @DisplayName("채팅방을 생성하고 생성된 채팅방을 완전히 삭제합니다.")
    void create_delete() {
        TradeStatus tradeStatus = TradeStatus.builder().id(1L).build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        ChatRoom createdChatRoom = createdRs.getData();
        assertThat(chatRoomService.findAll().getData()).hasSize(1);

        chatRoomService.delete(createdChatRoom);
        assertThat(chatRoomService.findAll().getData()).isEmpty();
    }

}
