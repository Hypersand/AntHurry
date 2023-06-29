package com.ant.hurry.boundedContext.coin.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class Exchange  extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member member;
    @Enumerated(EnumType.STRING)
    private BankType bankType;
    private String accountNumber;
    private String holderName;
    private long money;
    private boolean status;
}
