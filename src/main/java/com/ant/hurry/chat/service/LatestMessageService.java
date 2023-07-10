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
            LatestMessage latestMessage = latestMessageRepository.findByChatRoomId(cr.getId())
                    .orElseGet(() -> LatestMessage.builder()
                            .roomId(cr.getId())
                            .createdAt(LocalDateTime.now())
                            .build());
            latestMessages.add(latestMessage);
        });

        return RsData.of(MESSAGE_FOUND, latestMessages);
    }

    public RsData<LatestMessage> findByChatRoomId(String roomId) {
        LatestMessage latestMessage = latestMessageRepository.findByChatRoomId(roomId)
                .orElseGet(() -> LatestMessage.builder()
                        .roomId(roomId)
                        .createdAt(LocalDateTime.now())
                        .build());

        return RsData.of(MESSAGE_FOUND, latestMessage);
    }

    public void save(LatestMessage latestMessage) {
        latestMessageRepository.save(latestMessage);
    }
}
