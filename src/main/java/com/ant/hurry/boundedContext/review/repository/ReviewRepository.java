package com.ant.hurry.boundedContext.review.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByWriterAndTradeStatus(Member writer, TradeStatus tradeStatus);

    List<Review> findByReceiver(Member receiver);

    Long countByReceiver_Id(Long receiverId);
}
