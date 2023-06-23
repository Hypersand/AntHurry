package com.ant.hurry.boundedContext.board.dto;

import com.ant.hurry.boundedContext.board.entity.BoardType;
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
    private String address;
    private int rewardCoin;
    private double x;
    private double y;
    private String regCode;

    public CreateConvertDTO(double x, double y, String regCode){
        this.x = x;
        this.y = y;
        this.regCode = regCode;
    }

    public void buildCreateRequest(CreateRequest createRequest){
        CreateConvertDTO
                .builder()
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .boardType(createRequest.getBoardType())
                .rewardCoin(createRequest.getRewardCoin())
                .build();
    }

}
