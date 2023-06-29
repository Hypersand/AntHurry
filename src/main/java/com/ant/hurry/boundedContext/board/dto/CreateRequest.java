package com.ant.hurry.boundedContext.board.dto;

import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    @NotNull(message = "게시글 유형을 선택해주세요.")
    private BoardType boardType;
    @NotNull(message = "거래 유형을 선택해주세요.")
    private TradeType tradeType;
    private String address;
    @NotNull(message = "보상코인을 입력해주세요.")
    private int rewardCoin;

}
