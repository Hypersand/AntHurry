package com.ant.hurry.boundedContext.tradeStatus.service;

import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.repository.TradeStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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

}
