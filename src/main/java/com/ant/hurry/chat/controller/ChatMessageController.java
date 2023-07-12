package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatFileMessage;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.service.ChatMessageService;
import com.ant.hurry.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@Tag(name = "ChatMessageController" , description = "채팅 메시지 전반에 관한 컨트롤러")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Rq rq;

    @Operation(summary = "채팅 메시지 전송", description = "일반 채팅 메시지를 전송합니다.")
    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessageDto dto) {
        chatMessageService.send(dto);
        messagingTemplate.convertAndSend("/sub/chat/room/%s".formatted(dto.getRoomId()), dto);
    }

    @Operation(summary = "알림 메시지 전송", description = "상대방이 채팅방을 나갔을 때 자동으로 알림 메시지를 전송합니다.")
    public void sendExitMessage(String roomId) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setWriter("admin");
        dto.setRoomId(roomId);
        dto.setMessage("[알림]\n" + rq.getMember().getNickname() + " 님이 채팅방을 나가셨습니다.");

        chatMessageService.saveNoticeMessage(dto);
        messagingTemplate.convertAndSend("/sub/chat/room/%s".formatted(roomId), dto);
    }

    public void sendBackMessage(String roomId) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setWriter("admin");
        dto.setRoomId(roomId);
        dto.setMessage("[알림]\n" + rq.getMember().getNickname() + " 님이 재입장하셨습니다.");

        chatMessageService.saveNoticeMessage(dto);
        messagingTemplate.convertAndSend("/sub/chat/room/%s".formatted(roomId), dto);
    }

    @Operation(summary = "파일 메시지 저장 및 전송 요청", description = "파일이 첨부된 메시지의 파일을 저장하고 메시지 전송을 요청합니다.")
    @PostMapping("/file")
    public void sendFile(@RequestParam String roomId, @Payload MultipartFile file) throws IOException {
        ChatRoom chatRoom = chatRoomService.findById(roomId).getData();
        RsData<ChatFileMessage> rs = chatMessageService.sendFile(file, rq.getMember(), chatRoom);
        ChatFileMessage message = rs.getData();

        sendFileMessage(roomId, message);
    }

    @Operation(summary = "파일 메시지 전송", description = "파일이 첨부된 메시지를 전송합니다.")
    @MessageMapping("/chat/file")
    public void sendFileMessage(String roomId, ChatFileMessage message) {
        messagingTemplate.convertAndSend("/sub/chat/room/%s".formatted(roomId), message);
    }

    @Operation(summary = "파일 메시지 다운로드", description = "채팅 메시지를 통해 파일을 다운로드합니다.")
    @GetMapping("/download/{messageId}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable("messageId") String messageId) {
        RsData<ChatFileMessage> findRs = chatMessageService.findFileMessageById(messageId);

        if (findRs.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        ChatFileMessage message = findRs.getData();
        return chatMessageService.downloadFile(message);
    }

}
