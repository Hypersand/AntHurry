package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.chat.entity.LatestMessage;
import com.ant.hurry.chat.repository.LatestMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ant.hurry.chat.code.ChatMessageSuccessCode.MESSAGE_FOUND;

@Service
@RequiredArgsConstructor
public class LatestMessageService {

    private final LatestMessageRepository latestMessageRepository;

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
