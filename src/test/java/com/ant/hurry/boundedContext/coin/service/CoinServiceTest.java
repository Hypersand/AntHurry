package com.ant.hurry.boundedContext.coin.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CoinServiceTest {

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
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_E-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("충분한 코인을 가지고있습니다.")
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
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_E-2"),
                () -> assertThat(rsData.getMsg()).isEqualTo("0원을 환전할 수 없습니다.")
        );
    }

    @Test
    @DisplayName("환전 실패 - 환전은 내가 가지고있는 코인보다 낮아야한다.")
    @WithMockUser("user1")
    void shouldFailExchangeDueToNotEnoughMoney() {

        //given
        long exchangeMoney = 100000;

        //when
        RsData rsData = memberService.canExchange(exchangeMoney);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_E-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("충분한 돈을 가지고 있지 않습니다.")
        );
    }
}
