package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.entity.LatestMessage;
import com.ant.hurry.chat.repository.LatestMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_FOUND;

@Service
@RequiredArgsConstructor
public class LatestMessageService {

    private final LatestMessageRepository latestMessageRepository;
    private final ChatRoomService chatRoomService;

    public RsData<List<LatestMessage>> findByMember(Member member) {
        List<ChatRoom> chatRooms = chatRoomService.findByMember(member).getData();
        List<LatestMessage> latestMessages = new ArrayList<>();

        chatRooms.forEach(cr -> {
            LatestMessage latestMessage = latestMessageRepository.findByChatRoom(cr).orElse(null);
            latestMessages.add(latestMessage);
        });

        return RsData.of(MESSAGE_FOUND, latestMessages);
    }

    public RsData<LatestMessage> findByChatRoom(ChatRoom chatRoom) {
        LatestMessage latestMessage = latestMessageRepository.findByChatRoom(chatRoom)
                .orElseGet(() -> LatestMessage.builder()
                        .chatRoom(chatRoom)
                        .createdAt(LocalDateTime.now())
                        .build());

        return RsData.of(MESSAGE_FOUND, latestMessage);
    }

}
