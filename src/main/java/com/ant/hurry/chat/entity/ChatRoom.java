package com.ant.hurry.chat.entity;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Document(collection = "chat_room")
public class ChatRoom {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private TradeStatus tradeStatus;

    private ChatMessage latestMessage;

    @ManyToMany
    private List<Member> members;

    @ManyToMany
    private List<Member> exitedMembers; // member 두 명 모두 extiedMembers에 들어가게 되면 채팅방은 삭제됩니다.

    public void setLatestMessage(ChatMessage message) {
        this.latestMessage = message;
    }
}
