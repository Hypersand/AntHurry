package com.ant.hurry.chat.entity;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "deleted_room")
public class DeletedChatRoom {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdAt;

    private TradeStatus tradeStatus;

    @ManyToMany
    private List<Member> members;

}
