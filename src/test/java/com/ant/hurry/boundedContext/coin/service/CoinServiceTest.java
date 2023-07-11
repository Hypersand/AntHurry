package com.ant.hurry.boundedContext.coin.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.coin.dto.ExchangeRequest;
import com.ant.hurry.boundedContext.coin.entity.BankType;
import com.ant.hurry.boundedContext.coin.entity.Exchange;
import com.ant.hurry.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.ant.hurry.base.code.BasicErrorCode.UNAUTHORIZED;
import static com.ant.hurry.boundedContext.adm.code.AdmErrorCode.APPLY_NOT_EXISTS;
import static com.ant.hurry.boundedContext.coin.code.ExchangeErrorCode.*;
import static com.ant.hurry.boundedContext.coin.code.ExchangeSuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CoinServiceTest {

    @Autowired
    private CoinService coinService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("환전할 코인보다 많아야 환전할 수 있다.")
    @WithMockUser("user1")
    void can_exchange_enough_coin() {

        //given
        long exchangeMoney = 100;

        //when
        RsData rsData = memberService.canExchange(exchangeMoney);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(COIN_ENOUGH.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(COIN_ENOUGH.getMessage())
        );
    }

    @Test
    @DisplayName("환전 실패 - 0원은 환전할 수 없다.")
    @WithMockUser("user1")
    void shouldFailExchangeDueToExchangeZeroMoney() {

        //given
        long exchangeMoney = 0;

        //when
        RsData rsData = memberService.canExchange(exchangeMoney);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(CANNOT_EXCHANGE.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(CANNOT_EXCHANGE.getMessage())
        );
    }

    @Test
    @DisplayName("환전 실패 - 환전은 내가 가지고 있는 코인보다 낮아야한다.")
    @WithMockUser("user1")
    void shouldFailExchangeDueToNotEnoughMoney() {

        //given
        long exchangeMoney = 100000;

        //when
        RsData rsData = memberService.canExchange(exchangeMoney);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(COIN_NOT_ENOUGH.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(COIN_NOT_ENOUGH.getMessage())
        );
    }

    @Test
    @DisplayName("환전 신청 수정 - 환전신청이 존재하지 않는 경우.")
    @WithMockUser("user1")
    void shouldFailModifyApplyExchange() {

        //given
        ExchangeRequest exchange = new ExchangeRequest(BankType.기업, "123456789", "홍길순", 300);

        //when
        RsData rsData = coinService.modifyApplyExchange(exchange, 3L);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(APPLY_NOT_EXISTS.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(APPLY_NOT_EXISTS.getMessage())
        );
    }

    @Test
    @DisplayName("환전 신청 수정")
    @WithMockUser("user1")
    void shouldModifyApplyExchange() {

        //given
        ExchangeRequest exchange = new ExchangeRequest(BankType.기업, "123456789", "홍길순", 300);

        //when
        RsData rsData = coinService.modifyApplyExchange(exchange, 1L);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(EDIT_APPLY_EXCHANGE.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(EDIT_APPLY_EXCHANGE.getMessage())
        );
    }

    @Test
    @DisplayName("환전신청 취소 가능여부 테스트")
    @WithMockUser("user1")
    void shouldCanCancelExchange() {

        //when
        RsData rsData = coinService.canCancelExchange(1L);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(CAN_DELETE_APPLY_EXCHANGE.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(CAN_DELETE_APPLY_EXCHANGE.getMessage())
        );
    }

    @Test
    @DisplayName("환전신청 취소 가능여부 테스트 실패 - 존재하지 않는 환전신청일 경우")
    @WithMockUser("user1")
    void shouldFailCanCancelExchangeDueToNotExists() {

        //when
        RsData rsData = coinService.canCancelExchange(4L);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(NOT_EXISTS_APPLY_EXCHANGE.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(NOT_EXISTS_APPLY_EXCHANGE.getMessage())
        );
    }

    @Test
    @DisplayName("환전신청 취소 가능여부 테스트 실패 - 권한없음.")
    @WithMockUser("user3")
    void shouldFailCanCancelExchangeDueToAuthority() {

        //when
        RsData rsData = coinService.canCancelExchange(1L);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(UNAUTHORIZED.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(UNAUTHORIZED.getMessage())
        );
    }
}
