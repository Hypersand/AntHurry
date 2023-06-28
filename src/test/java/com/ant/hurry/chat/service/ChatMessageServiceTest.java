package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_DELETED;
import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_SENT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class ChatMessageServiceTest {

    @Autowired
    ChatMessageService chatMessageService;
    @Autowired
    ChatMessageRepository chatMessageRepository;

    @AfterEach
    @Transactional
    void refresh() {
        chatMessageRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅 메시지를 생성하고 저장합니다.")
    void chatMessage_create() {
        Member member = Member.builder().build();
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatMessageDto dto = ChatMessageDto.builder()
                .chatRoom(chatRoom)
                .content("안녕하세요.")
                .sender(member)
                .build();

        RsData<ChatMessage> rs = chatMessageService.create(dto);
        assertThat(rs.getResultCode()).isEqualTo(MESSAGE_SENT.getCode());
        assertThat(chatMessageRepository.findAll()).hasSize(1);
        assertThat(chatRoom.getLatestMessage().getContent()).isEqualTo("안녕하세요.");
    }

    @Test
    @DisplayName("채팅 메시지를 생성하고 생성된 메시지를 soft-delete 합니다.")
    void chatMessage_create_deleteSoft() {
        Member member = Member.builder().build();
        ChatRoom chatRoom = ChatRoom.builder().build();
        ChatMessageDto dto = ChatMessageDto.builder()
                .chatRoom(chatRoom)
                .content("안녕하세요.")
                .sender(member)
                .build();

        RsData<ChatMessage> createRs = chatMessageService.create(dto);
        ChatMessage message = createRs.getData();

        RsData deleteRs = chatMessageService.deleteSoft(message);
        assertThat(deleteRs.getResultCode()).isEqualTo(MESSAGE_DELETED.getCode());
    }

}
