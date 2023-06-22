package com.ant.hurry.boundedContext.notification.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member helper;

    protected Notification() {
    }

    public static Notification create(String message, Member requester, Member helper) {
        return Notification.builder()
                .message(message)
                .requester(requester)
                .helper(helper)
                .build();

    }
}
