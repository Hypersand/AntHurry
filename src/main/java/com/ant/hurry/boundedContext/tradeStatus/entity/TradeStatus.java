package com.ant.hurry.boundedContext.tradeStatus.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TradeStatus extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member helper;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    public String getRequesterUsername() {
        return requester.getUsername();
    }

    public String getHelperUsername() {
        return helper.getUsername();
    }

}
