package com.ant.hurry.boundedContext.review.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Mock
    private MemberService memberService;



    @Test
    @DisplayName("후기 등록 시 멤버를 찾을 수 없으면 등록할 수 없다.")
    void member_not_exists() {

        //given
        String username = "bigsand";
        ReviewRequest reviewRequest = new ReviewRequest("content", 5.0);
        Long tradeStatusId = 1L;

        given(memberService.findByUsername(username)).willReturn(Optional.empty());

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

    }

    @Test
    @DisplayName("올바른 요청 시 후기 정상 등록")
    void review_success_create() {


    }

}