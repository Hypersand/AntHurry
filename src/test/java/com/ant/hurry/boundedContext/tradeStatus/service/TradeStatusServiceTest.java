package com.ant.hurry.boundedContext.tradeStatus.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.repository.TradeStatusRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class TradeStatusServiceTest {

    @Autowired
    TradeStatusService tradeStatusService;
    @Autowired
    TradeStatusRepository tradeStatusRepository;

//    @Test
//    void createTradeStatus() {
//        board, requester, helper 생성 및 저장 후 이용
//        TradeStatus tradeStatus = tradeStatusService.create(board, requester, helper);
//
//        assertThat(tradeStatusRepository.findAll().size()).isEqualTo(1);
//        assertThat(tradeStatus.getStatus()).isEqualTo(Status.BEFORE);
//    }


    @Test
    @DisplayName("유효하지 않은 username으로 접근 시 오류 발생")
    void trade_member_not_exists() {

        //given
        String username = "member_not_exists";
        Status status = Status.COMPLETE;

        //when
        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(username, status);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_M-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("존재하지 않는 회원입니다.")
        );

    }

    @Test
    @DisplayName("tradeStatus가 COMPLETE인 거래 조회")
    void tradeStatus_COMPLETE() {

        //given
        String username = "user3";
        Status status = Status.COMPLETE;

        //when
        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(username, status);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_T-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("거래상태페이지로 이동합니다."),
                () -> assertThat(rsData.getData().size()).isEqualTo(3),
                () -> assertThat(rsData.getData()).isSortedAccordingTo(Comparator.comparing((TradeStatus e) -> e.getId()).reversed())

        );
    }

    @Test
    @DisplayName("tradeStatus가 BEFORE인 거래 조회")
    void tradeStatus_BEFORE() {

        //given
        String username = "user1";
        Status status = Status.BEFORE;

        //when
        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(username, status);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_T-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("거래상태페이지로 이동합니다."),
                () -> assertThat(rsData.getData().size()).isEqualTo(1),
                () -> assertThat(rsData.getData()).isSortedAccordingTo(Comparator.comparing((TradeStatus e) -> e.getId()).reversed())

        );
    }

    @Test
    @DisplayName("tradeStatus가 CANCELED인 거래 조회")
    void tradeStatus_CANCELED() {

        //given
        String username = "user3";
        Status status = Status.CANCELED;

        //when
        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(username, status);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_T-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("거래상태페이지로 이동합니다."),
                () -> assertThat(rsData.getData().size()).isEqualTo(2),
                () -> assertThat(rsData.getData()).isSortedAccordingTo(Comparator.comparing((TradeStatus e) -> e.getId()).reversed())

        );
    }


}
