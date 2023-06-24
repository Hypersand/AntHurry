package com.ant.hurry.boundedContext.review.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.repository.MemberRepository;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("후기 등록 시 멤버를 찾을 수 없으면 등록할 수 없다.")
    void member_not_exists() {

        //given
        String username = "member_not_exists";
        ReviewRequest reviewRequest = new ReviewRequest("content", 5.0);
        Long tradeStatusId = 1L;

        //when
        RsData<Review> rsData = reviewService.save(reviewRequest, username, tradeStatusId);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_M-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("존재하지 않는 회원입니다.")
        );

    }


    @Test
    @DisplayName("후기 등록 시 거래를 찾을 수 없으면 등록할 수 없다.")
    void tradeStatus_not_exists() {

        //given
        Member member = memberRepository.findByUsername("user1").orElse(null);
        ReviewRequest reviewRequest = new ReviewRequest("content", 4.0);
        Long tradeStatusId = 100L;

        //when
        RsData<Review> rsData = reviewService.save(reviewRequest, member.getUsername(), tradeStatusId);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_T-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("존재하지 않는 거래입니다.")
        );

    }

    @Test
    @DisplayName("올바른 요청 시 후기 정상 등록")
    void review_success_create() {

        //given
        Member member = memberRepository.findByUsername("user1").orElse(null);
        ReviewRequest reviewRequest = new ReviewRequest("content", 5.0);
        Long tradeStatusId = 1L;

        //when
        RsData<Review> rsData = reviewService.save(reviewRequest, member.getUsername(), tradeStatusId);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_R-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("후기가 성공적으로 등록되었습니다."),
                () -> assertThat(rsData.getData().getContent()).isEqualTo("content"),
                () -> assertThat(rsData.getData().getRating()).isEqualTo(5.0)
        );


    }

}