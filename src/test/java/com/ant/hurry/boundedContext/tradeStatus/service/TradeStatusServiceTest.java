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

import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusSuccessCode.CREATE_SUCCESS;
import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusSuccessCode.REDIRECT_TO_PAGE;
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
    void create_tradeStatus() {
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
        Status status = Status.BEFORE;

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
        Status status = Status.CANCELED;

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


}
