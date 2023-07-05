package com.ant.hurry.boundedContext.board.dto;

import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {

    private Long id;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private BoardType boardType;
    @Enumerated(EnumType.STRING)
    private TradeType tradeType;
    private int rewardCoin;
    private String regCode;
    private String nickname;

    public BoardDto(Board board) {
        this.id = board.getId();
        this.updatedAt = board.getUpdatedAt();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardType = board.getBoardType();
        this.tradeType = board.getTradeType();
        this.rewardCoin = board.getRewardCoin();
        this.regCode = board.getRegCode();
        this.nickname = board.getMember().getNickname();
    }

}
