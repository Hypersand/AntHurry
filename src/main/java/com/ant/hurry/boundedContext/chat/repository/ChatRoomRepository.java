package com.ant.hurry.boundedContext.chat.repository;

import com.ant.hurry.boundedContext.chat.entity.ChatRoom;
import com.ant.hurry.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByMember(Member member);
}
