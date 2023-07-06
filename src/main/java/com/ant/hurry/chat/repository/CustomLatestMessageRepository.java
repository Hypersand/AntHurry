package com.ant.hurry.chat.repository;

import com.ant.hurry.chat.entity.LatestMessage;

import java.util.Optional;

public interface CustomLatestMessageRepository {
    Optional<LatestMessage> findByChatRoomId(String roomId);
}
