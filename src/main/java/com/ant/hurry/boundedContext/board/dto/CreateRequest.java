package com.ant.hurry.boundedContext.board.dto;

import com.ant.hurry.boundedContext.board.entity.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateRequest {

    @NotBlank(message = "제목이 없습니다.")
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private BoardType boardType;
    @NotBlank
    private String address;
    @NotNull
    private int rewardCoin;

}
