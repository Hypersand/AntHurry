package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatFileMessage;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatFileMessageRepository;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static com.ant.hurry.chat.code.ChatMessageSuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatMessageServiceTest {

    @Autowired
    ChatMessageService chatMessageService;
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    ChatFileMessageRepository chatFileMessageRepository;

    @BeforeEach
    @AfterAll
    void refresh() {
        chatMessageRepository.deleteAll();
        chatFileMessageRepository.deleteAll();
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

        RsData<ChatMessage> rs = chatMessageService.send(dto);
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

        RsData<ChatMessage> createRs = chatMessageService.send(dto);
        ChatMessage message = createRs.getData();

        RsData deleteRs = chatMessageService.deleteSoft(message);
        assertThat(deleteRs.getResultCode()).isEqualTo(MESSAGE_DELETED.getCode());
    }

    @Test
    @DisplayName("채팅 메시지를 통해 파일을 전송합니다.")
    void chatFileMessage_sendFile() throws IOException {
        Member member = Member.builder().build();
        ChatRoom chatRoom = ChatRoom.builder().build();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );

        RsData<ChatFileMessage> rs = chatMessageService.sendFile(file, member, chatRoom);
        assertThat(rs.getResultCode()).isEqualTo(MESSAGE_SENT.getCode());

        String fileId = rs.getData().getUploadFileId();
        ChatFileMessage savedMessage = chatFileMessageRepository.findByUploadFileId(fileId).orElse(null);

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getUploadFileId()).isEqualTo(fileId);
    }

}
