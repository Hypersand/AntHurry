package com.ant.hurry.boundedContext.board.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.board.dto.CreateConvertDTO;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Board extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    private String title;

    private String content;

    private double x;

    private double y;

    private int rewardCoin;

    private String regCode;


    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void updateBoard(CreateRequest createRequest) {
        this.title = createRequest.getTitle();
        this.content = createRequest.getContent();
        this.boardType = createRequest.getBoardType();
        this.tradeType = createRequest.getTradeType();
        this.rewardCoin = createRequest.getRewardCoin();
    }

    public void updateBoard(CreateConvertDTO convertDTO) {
        this.title = convertDTO.getTitle();
        this.content = convertDTO.getContent();
        this.boardType = convertDTO.getBoardType();
        this.tradeType = convertDTO.getTradeType();
        this.rewardCoin = convertDTO.getRewardCoin();
        this.x = convertDTO.getX();
        this.y = convertDTO.getY();
        this.regCode = convertDTO.getRegCode();
    }


}
