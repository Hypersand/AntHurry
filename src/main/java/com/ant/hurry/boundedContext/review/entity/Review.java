package com.ant.hurry.boundedContext.review.entity;


import com.ant.hurry.base.baseEntity.BaseEntity;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Review extends BaseEntity {

    private String content;

    private double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    private TradeStatus tradeStatus;

    public static Review create(String content, double rating, TradeStatus tradeStatus) {
        return Review.builder()
                .content(content)
                .rating(rating)
                .tradeStatus(tradeStatus)
                .build();
    }

}
