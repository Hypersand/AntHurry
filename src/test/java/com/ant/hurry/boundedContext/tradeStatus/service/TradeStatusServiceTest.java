package com.ant.hurry.boundedContext.tradeStatus.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.repository.TradeStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusErrorCode.ALREADY_COMPLETED;
import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusErrorCode.COMPLETE_FAILED;
import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusSuccessCode.*;
import static com.ant.hurry.boundedContext.tradeStatus.entity.Status.*;
import static com.ant.hurry.boundedContext.tradeStatus.entity.Status.INPROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class TradeStatusServiceTest {

    @Autowired
    TradeStatusService tradeStatusService;

    @Test
    @DisplayName("신규 거래 상태를 생성합니다.")
    void tradeStatus_create() {
        TradeStatusRepository tradeStatusRepository = Mockito.mock(TradeStatusRepository.class);
        MemberService memberService = Mockito.mock(MemberService.class);

        Member requester = Member.builder().build();
        Member helper = Member.builder().build();
        Board board = Board.builder().member(requester).build();

        when(tradeStatusRepository.save(any(TradeStatus.class))).thenReturn(new TradeStatus());
        when(memberService.getMember()).thenReturn(requester);

        TradeStatusService tradeStatusService = new TradeStatusService(tradeStatusRepository, memberService);

        RsData<TradeStatus> rs = tradeStatusService.create(board, requester, helper);

        assertEquals(CREATE_SUCCESS.getCode(), rs.getResultCode());
        assertEquals(CREATE_SUCCESS.getMessage(), rs.getMsg());
        assertNotNull(rs.getData());
    }

    @Test
    @DisplayName("유효하지 않은 username으로 접근 시 오류 발생")
    void trade_member_not_exists() {

        //given
        String username = "member_not_exists";
        Status status = COMPLETE;

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
        Status status = COMPLETE;

        //when
        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(username, status);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(REDIRECT_TO_PAGE.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(REDIRECT_TO_PAGE.getMessage()),
                () -> assertThat(rsData.getData().size()).isEqualTo(3),
                () -> assertThat(rsData.getData()).isSortedAccordingTo(Comparator.comparing((TradeStatus e) -> e.getId()).reversed())

        );
    }

    @Test
    @DisplayName("tradeStatus가 BEFORE인 거래 조회")
    void tradeStatus_BEFORE() {

        //given
        String username = "user1";
        Status status = BEFORE;

        //when
        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(username, status);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(REDIRECT_TO_PAGE.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(REDIRECT_TO_PAGE.getMessage()),
                () -> assertThat(rsData.getData().size()).isEqualTo(1),
                () -> assertThat(rsData.getData()).isSortedAccordingTo(Comparator.comparing((TradeStatus e) -> e.getId()).reversed())

        );
    }

    @Test
    @DisplayName("tradeStatus가 CANCELED인 거래 조회")
    void tradeStatus_CANCELED() {

        //given
        String username = "user3";
        Status status = CANCELED;

        //when
        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(username, status);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo(REDIRECT_TO_PAGE.getCode()),
                () -> assertThat(rsData.getMsg()).isEqualTo(REDIRECT_TO_PAGE.getMessage()),
                () -> assertThat(rsData.getData().size()).isEqualTo(2),
                () -> assertThat(rsData.getData()).isSortedAccordingTo(Comparator.comparing((TradeStatus e) -> e.getId()).reversed())

        );
    }

    @Test
    @DisplayName("유효한 거래 완료 횟수를 반환한다.")
    void tradeStatus_count_valid() {

        //given
        Long memberId = 6L;

        //when
        Long count = tradeStatusService.getComleteTradeStatusCount(memberId);

        //then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("거래 상태를 INPROGRESS로 변경합니다.")
    void tradeStatus_updateToInprogress() {
        List<TradeStatus> tradeStatuses = tradeStatusService.findMyTradeStatusList("user1", BEFORE).getData();
        TradeStatus beforeTradeStatus = tradeStatuses.get(0);

        RsData<TradeStatus> rs = tradeStatusService.updateStatus(beforeTradeStatus, INPROGRESS);

        assertEquals(UPDATE_SUCCESS.getCode(), rs.getResultCode());
        assertEquals(UPDATE_SUCCESS.getMessage(), rs.getMsg());
        assertNotNull(rs.getData());
        assertEquals(rs.getData().getStatus(), INPROGRESS);
    }

    @Test
    @DisplayName("이미 완료된 거래를 INPROGRESS로 변경 시도합니다.")
    void tradeStatus_fail_updateToInprogress() {
        TradeStatus completeTradeStatus = TradeStatus.builder().status(COMPLETE).build();
        RsData<TradeStatus> rs = tradeStatusService.updateStatus(completeTradeStatus, INPROGRESS);

        assertEquals(ALREADY_COMPLETED.getCode(), rs.getResultCode());
        assertEquals(ALREADY_COMPLETED.getMessage(), rs.getMsg());
    }


    @Test
    @DisplayName("거래 상태를 COMPLETE로 변경합니다.")
    void tradeStatus_updateToComplete() {
        TradeStatus inprogressTradeStatus = TradeStatus.builder().status(INPROGRESS).build();
        RsData<TradeStatus> rs = tradeStatusService.updateStatus(inprogressTradeStatus, COMPLETE);

        assertEquals(UPDATE_SUCCESS.getCode(), rs.getResultCode());
        assertEquals(UPDATE_SUCCESS.getMessage(), rs.getMsg());
        assertNotNull(rs.getData());
        assertEquals(rs.getData().getStatus(), COMPLETE);
    }

    @Test
    @DisplayName("취소된 거래를 COMPLETE로 변경 시도합니다.")
    void tradeStatus_fail_updateToComplete() {
        List<TradeStatus> tradeStatuses = tradeStatusService.findMyTradeStatusList("user1", CANCELED).getData();
        TradeStatus canceledTradeStatus = tradeStatuses.get(0);

        RsData<TradeStatus> rs = tradeStatusService.updateStatus(canceledTradeStatus, COMPLETE);

        assertEquals(COMPLETE_FAILED.getCode(), rs.getResultCode());
        assertEquals(COMPLETE_FAILED.getMessage(), rs.getMsg());
    }

}
