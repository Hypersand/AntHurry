package com.ant.hurry.boundedContext.review.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByWriterAndTradeStatus(Member writer, TradeStatus tradeStatus);
}
