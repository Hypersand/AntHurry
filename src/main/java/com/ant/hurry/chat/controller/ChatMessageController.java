package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatFileMessage;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.service.ChatMessageService;
import com.ant.hurry.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final Rq rq;

    @MessageMapping("/room/{id}/message")
    public void sendMessage(@PathVariable("id") String roomId, ChatMessageDto dto) {
        RsData<ChatMessage> rs = chatMessageService.create(dto);
        ChatMessage message = rs.getData();
        messagingTemplate.convertAndSend("/sub/chat/room/%s".formatted(roomId), message);
    }

    @MessageMapping("/room/{id}/file/message")
    public void sendFile(
            @PathVariable("id") String roomId,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        ChatRoom chatRoom = chatRoomService.findById(roomId).getData();
        RsData<ChatFileMessage> rs = chatMessageService.sendFile(file, rq.getMember(), chatRoom);
        ChatFileMessage message = rs.getData();
        messagingTemplate.convertAndSend("/sub/chat/room/%s".formatted(roomId), message);
    }

    @GetMapping("/room/{id}/download/{messageId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("id") String roomId,
            @PathVariable("messageId") String messageId
    ) {
        RsData<ChatFileMessage> findRs = chatMessageService.findFileMessageById(messageId);
        if(findRs.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        RsData<Resource> resourceRs = chatMessageService.findFileByChatFileMessage(findRs.getData());
        Resource resource = resourceRs.getData();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
