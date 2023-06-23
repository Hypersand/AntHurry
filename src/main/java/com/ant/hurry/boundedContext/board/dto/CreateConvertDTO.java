package com.ant.hurry.boundedContext.board.dto;

import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateConvertDTO {

    private String title;
    private String content;
    private BoardType boardType;
    private TradeType tradeType;
    private String address;
    private int rewardCoin;
    private double x;
    private double y;
    private String regCode;
}
