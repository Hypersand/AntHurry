package com.ant.hurry.boundedContext.review.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "후기 작성은 필수입니다!")
    private String content;

    @Min(value = 1, message = "별점을 선택해주세요!")
    private double rating = 5.0;

    private String receiverName;


    public void updateReceiverName(String nickname) {
        this.receiverName = nickname;
    }

}
