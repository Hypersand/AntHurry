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

        //거래 상태 페이지 접속 -> 거래 완료 시 리뷰 등록 버튼 활성화 -> 버튼 누르면 리뷰 등록 페이지 -> 리뷰 작성

        Member member = memberService.findByUsername(username).orElse(null);
        TradeStatus tradeStatus = tradeStatusService.findById(tradeStatusId);

        if (member == null) {
            return RsData.of("F_M-1", "존재하지 않는 회원입니다.");
        }

        if (tradeStatus == null) {
            return RsData.of("F_T-1", "존재하지 않는 거래입니다.");
        }

        Review review = Review.create(reviewRequest.getContent(), reviewRequest.getRating(), tradeStatus);
        reviewRepository.save(review);

        return RsData.of("S_R-1", "후기가 성공적으로 등록되었습니다.", review);
    }
}
