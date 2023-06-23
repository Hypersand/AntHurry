package com.ant.hurry.boundedContext.chat.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ChatRoom extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private TradeStatus tradeStatus;

    private LocalDateTime deletedAt;

}
