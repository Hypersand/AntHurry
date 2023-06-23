package com.ant.hurry.boundedContext.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "후기 작성은 필수입니다!")
    private String content;

    @NotNull(message = "별점 선택은 필수입니다!")
    private double rating;

}
