package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatFileMessage;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatFileMessageRepository;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import com.ant.hurry.chat.repository.LatestMessageRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.UUID;

import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_DELETED;
import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_SENT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatMessageServiceTest {

    @Autowired
    ChatMessageService chatMessageService;
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    ChatFileMessageRepository chatFileMessageRepository;
    @Autowired
    LatestMessageRepository latestMessageRepository;

    private Member member;
    private ChatRoom chatRoom;
    private ChatMessageDto dto;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        member = Member.builder().username("user1").nickname("User1").build();
        chatRoom = ChatRoom.builder().id(UUID.randomUUID().toString()).build();
        dto = new ChatMessageDto();
        dto.setRoomId(chatRoom.getId());
        dto.setMessage("안녕하세요.");
        dto.setWriter(member.getUsername());
        file = new MockMultipartFile(
                "file",
                "testFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );
    }

    @AfterEach
    void refresh() {
        chatMessageRepository.deleteAll();
        chatFileMessageRepository.deleteAll();
        latestMessageRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅 메시지를 생성하고 저장합니다.")
    void chatMessage_create() {
        RsData<ChatMessage> rs = chatMessageService.send(dto);
        assertThat(rs.getResultCode()).isEqualTo(MESSAGE_SENT.getCode());
        assertThat(chatMessageRepository.findAll()).hasSize(1);
        assertThat(latestMessageRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("채팅 메시지를 생성하고 생성된 메시지를 soft-delete 합니다.")
    void chatMessage_create_deleteSoft() {
        RsData<ChatMessage> createRs = chatMessageService.send(dto);
        ChatMessage message = createRs.getData();

        RsData deleteRs = chatMessageService.deleteSoft(message);
        assertThat(deleteRs.getResultCode()).isEqualTo(MESSAGE_DELETED.getCode());
    }

    @Test
    @DisplayName("채팅 메시지를 통해 파일을 전송합니다.")
    void chatFileMessage_sendFile() throws IOException {
        RsData<ChatFileMessage> rs = chatMessageService.sendFile(file, member, chatRoom);
        assertThat(rs.getResultCode()).isEqualTo(MESSAGE_SENT.getCode());
        assertThat(latestMessageRepository.findAll()).hasSize(1);

        String fileId = rs.getData().getUploadFileId();
        ChatFileMessage savedMessage = chatFileMessageRepository.findByUploadFileId(fileId).orElse(null);

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getUploadFileId()).isEqualTo(fileId);
        assertThat(savedMessage.getUploadFilePath())
                .isEqualTo("fs/" + savedMessage.getUploadFileId());
    }

    @Test
    @DisplayName("전송된 메시지를 통해 파일을 다운로드합니다.")
    void chatFileMessage_downloadFile() throws IOException {
        RsData<ChatFileMessage> rsData = chatMessageService.sendFile(file, member, chatRoom);
        ChatFileMessage chatFileMessage = rsData.getData();

        ResponseEntity<StreamingResponseBody> responseEntity = chatMessageService.downloadFile(chatFileMessage);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
