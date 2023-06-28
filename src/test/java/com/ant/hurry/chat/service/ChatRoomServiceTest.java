package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import com.ant.hurry.chat.repository.DeletedChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class ChatRoomServiceTest {

    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    DeletedChatRoomRepository deletedChatRoomRepository;

    @AfterEach
    @Transactional
    void refresh() {
        chatRoomRepository.deleteAll();
        deletedChatRoomRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방을 생성하고 생성된 채팅방을 조회합니다.")
    void chatRoom_create_findById() {
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        TradeStatus tradeStatus = TradeStatus.builder().requester(member1).helper(member2).build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        RsData<ChatRoom> findRs = chatRoomService.findById(createdRs.getData().getId());

        assertThat(findRs.getData()).isNotNull();
    }

    @Test
    @DisplayName("채팅방을 생성하고 생성된 채팅방을 삭제합니다.")
    void chatRoom_create_delete() {
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        TradeStatus tradeStatus = TradeStatus.builder().requester(member1).helper(member2).build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);
        ChatRoom createdChatRoom = createdRs.getData();
        assertThat(chatRoomService.findAll().getData()).hasSize(1);

        chatRoomService.delete(createdChatRoom);
        assertThat(chatRoomService.findAll().getData()).isEmpty();
        assertThat(deletedChatRoomRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("모든 멤버가 채팅방을 나가면 채팅방은 자동으로 삭제됩니다.")
    void chatRoom_create_delete_whenMemberExit() {
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        TradeStatus tradeStatus = TradeStatus.builder().requester(member1).helper(member2).build();
        RsData<ChatRoom> createdRs = chatRoomService.create(tradeStatus);

        ChatRoom createdChatRoom = createdRs.getData();
        assertThat(chatRoomService.findAll().getData()).hasSize(1);

        chatRoomService.exit(createdChatRoom, member1);
        chatRoomService.exit(createdChatRoom, member2);

        assertThat(chatRoomService.findAll().getData()).isEmpty();
        assertThat(deletedChatRoomRepository.findAll()).hasSize(1);
    }

}
