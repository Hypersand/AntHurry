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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @DisplayName("거래가 완료되지 않았으면 후기를 작성할 수 없다.")
    @WithMockUser("user1")
    void review_tradeStatus_not_complete() {

        //given
        Long tradeStatusId = 1L;

        //when
        RsData<Object> rsData = reviewService.validateTradeStatusAndMember(tradeStatusId, "user1");

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_T-2"),
                () -> assertThat(rsData.getMsg()).isEqualTo("아직 리뷰를 남길 수 없는 거래입니다.")
        );

    }

    @Test
    @DisplayName("이미 후기를 작성했으면 또 작성할 수 없다.")
    @WithMockUser("user2")
    void review_already_written() {

        //given
        Long tradeStatusId = 3L;

        //when
        RsData<Object> rsData = reviewService.validateTradeStatusAndMember(tradeStatusId, "user2");

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_R-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("이미 후기를 작성했습니다.")
        );

    }

    @Test
    @DisplayName("해당 거래의 requester도 helper도 아니면 후기 등록 페이지에 접근할 수 없다.")
    @WithMockUser("user1")
    void review_member_not_authority() {

        //given
        Long tradeStatusId = 3L;

        //when
        RsData<Object> rsData = reviewService.validateTradeStatusAndMember(tradeStatusId, "user1");

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_M-2"),
                () -> assertThat(rsData.getMsg()).isEqualTo("접근할 수 있는 권한이 없습니다.")
        );

    }

    @Test
    @DisplayName("후기 등록 페이지에 정상 접속")
    @WithMockUser("user3")
    void review_success_connect() {

        //given
        Long tradeStatusId = 3L;

        //when
        RsData<Object> rsData = reviewService.validateTradeStatusAndMember(tradeStatusId, "user3");

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_R-2"),
                () -> assertThat(rsData.getMsg()).isEqualTo("후기등록페이지로 이동합니다."),
                () -> assertThat(rsData.getData()).isEqualTo("User2")
        );

    }

    @Test
    @DisplayName("후기 등록 시 멤버를 찾을 수 없으면 등록할 수 없다.")
    void member_not_exists() {

        //given
        String username = "member_not_exists";
        ReviewRequest reviewRequest = new ReviewRequest("content", 5.0, "User2");
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
        ReviewRequest reviewRequest = new ReviewRequest("content", 4.0, "User4");
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
        ReviewRequest reviewRequest = new ReviewRequest("content", 5.0, "User2");
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

    @Test
    @DisplayName("후기 정상 등록 시 평점이 제대로 계산되는지 확인")
    @WithMockUser("user1")
    void review_rating_success() {

        //given
        Member member = memberRepository.findByUsername("user1").orElse(null);
        ReviewRequest reviewRequest = new ReviewRequest("content", 5.0, "User3");
        Long tradeStatusId = 2L;

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

    @Test
    @DisplayName("유효하지 않은 멤버로 후기 목록 페이지 접근")
    void review_list_member_not_exists() {


        //when
        RsData<List<Review>> rsData = reviewService.getMyReviews("member_not_exists");

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_M-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("존재하지 않는 회원입니다.")
        );
    }

    @Test
    @DisplayName("유효한 멤버로 후기 목록 페이지 접근")
    void review_list_member_exists() {

        //when
        RsData<List<Review>> rsData = reviewService.getMyReviews("user1");

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_R-3"),
                () -> assertThat(rsData.getMsg()).isEqualTo("후기목록페이지로 이동합니다."),
                () -> assertThat(rsData.getData().get(0).getContent()).isEqualTo("내용3"),
                () -> assertThat(rsData.getData().get(0).getRating()).isEqualTo(2.0)
        );

    }


}