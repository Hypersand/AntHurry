package com.ant.hurry.boundedContext.review.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.review.repository.ReviewRepository;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final MemberService memberService;

    private final TradeStatusService tradeStatusService;
    private final ReviewRepository reviewRepository;

    public RsData<Review> save(ReviewRequest reviewRequest, String username, Long tradeStatusId) {

        Member member = memberService.findByUsername(username).orElse(null);
        TradeStatus tradeStatus = tradeStatusService.findById(tradeStatusId);

        if (member == null) {
            return RsData.of("F_M-1", "존재하지 않는 회원입니다.");
        }

        if (tradeStatus == null) {
            return RsData.of("F_T-1", "존재하지 않는 거래입니다.");
        }

        Review review = Review.create(reviewRequest.getContent(), reviewRequest.getRating(), tradeStatus, member);
        reviewRepository.save(review);

        double opponentRating;

        if (member.equals(tradeStatus.getRequester())) {
            opponentRating = calculateRating(tradeStatus.getHelper(), review.getRating());
            tradeStatus.getHelper().updateRating(opponentRating);
        }

        else {
            opponentRating = calculateRating(tradeStatus.getRequester(), review.getRating());
            tradeStatus.getRequester().updateRating(opponentRating);
        }

        return RsData.of("S_R-1", "후기가 성공적으로 등록되었습니다.", review);
    }


    public boolean isAlreadyReviewed(Member member, TradeStatus tradeStatus) {

        Review review = reviewRepository.findByWriterAndTradeStatus(member, tradeStatus).orElse(null);

        if (review == null) {
            return false;
        }

        return true;
    }

    private double calculateRating(Member opponent, double rating) {
        double opponentRating = opponent.getRating();
        int reviewCount = opponent.getReviewCount();

        double avgRating = (opponentRating * reviewCount + rating) / (reviewCount + 1);

        return avgRating;
    }
}
