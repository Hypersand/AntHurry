package com.ant.hurry.boundedContext.board.dto;

import com.ant.hurry.boundedContext.board.entity.BoardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateConvertDTO {

    private String title;
    private String content;
    private BoardType boardType;
    private String address;
    private int rewardCoin;
    private double x;
    private double y;
    private int regCode;

    public CreateConvertDTO(int x, int y, int regCode){
        this.x = x;
        this.y = y;
        this.regCode = regCode;
    }

    public void convertData(CreateRequest createRequest){
        CreateConvertDTO
                .builder()
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .boardType(createRequest.getBoardType())
                .rewardCoin(createRequest.getRewardCoin())
                .build();
    }

}
