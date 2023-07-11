package com.ant.hurry.boundedContext.notification.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

    private String message;

    @Enumerated(EnumType.STRING)
    private NotifyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member helper;

    private LocalDateTime readDate;

    private Long tradeId;

    public Notification() {
    }

    public static Notification create(String message, String type, Member requester, Member helper) {
        return Notification.builder()
                .message(message)
                .type(NotifyType.valueOf(type))
                .requester(requester)
                .helper(helper)
                .build();
    }

    public static Notification create(String message, String type, Member requester, Member helper, Long tradeId) {
        return Notification.builder()
                .message(message)
                .type(NotifyType.valueOf(type))
                .requester(requester)
                .helper(helper)
                .tradeId(tradeId)
                .build();
    }

    public boolean isRead() {
        return readDate != null;
    }

    public void markRead() {
        readDate = LocalDateTime.now();
    }
}
