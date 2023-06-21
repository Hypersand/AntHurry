package com.ant.hurry.boundedContext.board.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Board extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    private String title;

    private String content;

    private double x;

    private double y;

    private int rewardCoin;

    private int regCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;


}
