package com.ant.hurry.boundedContext.review.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.repository.ProfileImageRepository;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.review.repository.ReviewRepository;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ant.hurry.boundedContext.member.code.MemberErrorCode.*;
import static com.ant.hurry.boundedContext.review.code.ReviewErrorCode.*;
import static com.ant.hurry.boundedContext.review.code.ReviewSuccessCode.*;
import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final MemberService memberService;

    private final TradeStatusService tradeStatusService;
    private final ReviewRepository reviewRepository;

    private final ProfileImageRepository profileImageRepository;

    public RsData<Review> save(ReviewRequest reviewRequest, String username, Long tradeStatusId) {

        Member member = memberService.findByUsername(username).orElse(null);
        Member receiver = memberService.findByNickname(reviewRequest.getReceiverName()).orElse(null);
        TradeStatus tradeStatus = tradeStatusService.findById(tradeStatusId).getData();

        if (member == null) {
            return RsData.of(MEMBER_NOT_EXISTS);
        }

        if (receiver == null) {
            return RsData.of(MEMBER_NOT_EXISTS);
        }

        if (tradeStatus == null) {
            return RsData.of(TRADESTATUS_NOT_EXISTS);
        }

        Review review = Review.create(reviewRequest.getContent(), reviewRequest.getRating(), tradeStatus, member, receiver);
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

        return RsData.of(CREATE_SUCCESS, review);
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

        avgRating = Math.round(avgRating * 10) / 10.0;

        return avgRating;
    }

    public RsData<Object> validateTradeStatusAndMember(Long tradeStatusId, String username) {

        TradeStatus tradeStatus = tradeStatusService.findById(tradeStatusId).getData();
        Member member = memberService.findByUsername(username).orElse(null);

        if (!member.getUsername().equals(tradeStatus.getRequesterUsername()) && !member.getUsername().equals(tradeStatus.getHelperUsername())) {
            return RsData.of(CANNOT_ACCESS);
        }

        if (!tradeStatus.getStatus().name().equals("COMPLETE")) {
            return RsData.of(CANNOT_WRITE_REVIEW);
        }

        if (isAlreadyReviewed(member, tradeStatus)) {
            return RsData.of(ALREADY_WRITE_REVIEW);
        }

        if (tradeStatus.getRequesterUsername().equals(member.getUsername())) {
            return RsData.of(REDIRECT_TO_CREATE_REVIEW_PAGE, tradeStatus.getHelper().getNickname());
        }

        return RsData.of(REDIRECT_TO_CREATE_REVIEW_PAGE, tradeStatus.getRequester().getNickname());
    }

    public RsData<Map<String, Object>> getReviews(String username, Long memberId) {

        Member currentMember = memberService.findByUsername(username).orElse(null);
        Member profileMember = memberService.findById(memberId).orElse(null);

        if (currentMember == null || profileMember == null) {
            return RsData.of(MEMBER_NOT_EXISTS);
        }

        Map<String, Object> map = new HashMap<>();

        List<Review> reviews = reviewRepository.findByReceiver(profileMember);
        List<ProfileImage> reviewerProfileImage = new ArrayList<>();
        for (Review review : reviews) {
            reviewerProfileImage.add(profileImageRepository.findByMember(review.getWriter()).orElse(null));
        }
        map.put("reviews", reviews);
        map.put("reviewerProfileImage", reviewerProfileImage);
        map.put("profileMember", profileMember);

        return RsData.of(REDIRECT_TO_REVIEW_LIST_PAGE, map);
    }

    public Long getReviewCount(Long id) {
        return reviewRepository.countByReceiver_Id(id);
    }

    public Long getMyReviewCount(Long id){
        return reviewRepository.countByWriter_Id(id);
    }

}
