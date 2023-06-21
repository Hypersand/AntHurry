package com.ant.hurry.boundedContext.board.dto;

import com.ant.hurry.boundedContext.board.entity.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    @Setter
    private double x;
    @Setter
    private double y;
    @Setter
    private int regCode;

    public void addressConvert(double x, double y, int regCode){
        this.x = x;
        this.y = y;
        this.regCode = regCode;
    }

}
