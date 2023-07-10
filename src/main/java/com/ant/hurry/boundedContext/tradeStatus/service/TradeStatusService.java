package com.ant.hurry.boundedContext.tradeStatus.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.event.EventAfterDeletedTradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.event.EventAfterUpdateStatus;
import com.ant.hurry.boundedContext.tradeStatus.repository.TradeStatusRepository;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusErrorCode.*;
import static com.ant.hurry.boundedContext.tradeStatus.code.TradeStatusSuccessCode.*;
import static com.ant.hurry.boundedContext.tradeStatus.entity.Status.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeStatusService {

    private final TradeStatusRepository tradeStatusRepository;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public RsData<TradeStatus> create(Board board, Member requester, Member helper) {
        TradeStatus tradeStatus = TradeStatus.builder()
                .status(BEFORE)
                .board(board)
                .requester(requester)
                .helper(helper)
                .build();
        tradeStatusRepository.save(tradeStatus);
        return RsData.of(CREATE_SUCCESS, tradeStatus);
    }

    @Transactional
    public RsData<TradeStatus> updateStatus(TradeStatus tradeStatus, Status status) {
        RsData canUpdateStatus = canUpdateStatus(tradeStatus, status);

        if (canUpdateStatus.isFail()) {
            return canUpdateStatus;
        }

        TradeStatus modifiedTradeStatus = tradeStatus.toBuilder().status(status).build();
        tradeStatusRepository.save(modifiedTradeStatus);

        ChatRoom chatRoom = chatRoomService.findByTradeStatusId(modifiedTradeStatus.getId()).getData();
        chatRoomService.updateStatusOfChatRoom(chatRoom, status);

        if(status.equals(COMPLETE)){
            publisher.publishEvent(new EventAfterUpdateStatus(tradeStatus));
        }


        return RsData.of(UPDATE_SUCCESS, modifiedTradeStatus);
    }

    private RsData canUpdateStatus(TradeStatus tradeStatus, Status status) {
        Status target = tradeStatus.getStatus();
        if (!(target.equals(BEFORE) || target.equals(COMPLETE)) && status.equals(INPROGRESS)) {
            return RsData.of(ALREADY_IN_PROGRESS);
        }
        if (!target.equals(INPROGRESS) && status.equals(COMPLETE)) {
            return RsData.of(COMPLETE_FAILED);
        }
        if (target.equals(COMPLETE) && !status.equals(CANCELED)) {
            return RsData.of(ALREADY_COMPLETED);
        }
        return RsData.of(CAN_UPDATE);
    }

    public RsData<List<TradeStatus>> findByMember(Member member) {
        return RsData.of(TRADESTATUS_FOUND, tradeStatusRepository.findByRequesterOrHelper(member.getId()));
    }

    public RsData<TradeStatus> findById(Long id) {
        Optional<TradeStatus> tradeStatus = tradeStatusRepository.findById(id);
        return tradeStatus.map(status -> RsData.of(TRADESTATUS_FOUND, status))
                .orElseGet(() -> RsData.of(TRADESTATUS_NOT_EXISTS));
    }

    public RsData<List<TradeStatus>> findMyTradeStatusList(String username, Status status) {

        Member member = memberService.findByUsername(username).orElse(null);

        if (member == null) {
            return RsData.of("F_M-1", "존재하지 않는 회원입니다.");
        }

        List<TradeStatus> tradeStatusList = tradeStatusRepository.findMyTradeStatus(member.getId(), status);

        return RsData.of(REDIRECT_TO_PAGE, tradeStatusList);
    }

    public Long getComleteTradeStatusCount(Long id) {
        return tradeStatusRepository.countMemberCompleteTradeStatus(id);

    }

    public Optional<TradeStatus> checkExistStatus(Long boardId, Long memberId) {
        return tradeStatusRepository.findByBoardIdAndMemberId(boardId, memberId);
    }

    public boolean isAlreadyCompletedTrade(Long boardId) {
        TradeStatus tradeStatus = tradeStatusRepository.findByBoardIdAndCompleteStatus(boardId).orElse(null);

        if (tradeStatus == null) {
            return false;
        }

        return true;
    }

    public Long getMemberTradeStatusCount(Long memberId){
        return tradeStatusRepository.countMemberTradeStatus(memberId);
    }

    public void deleteTradeStatusDueToBoard(Long boardId){
        publisher.publishEvent(new EventAfterDeletedTradeStatus(tradeStatusRepository.findByBoardId(boardId)));
        tradeStatusRepository.deleteByBoardId(boardId);
    }

    public void updateOtherTradeToCancel(Board board) {
        List<TradeStatus> tradeStatuses = tradeStatusRepository.findByBoardAndStatusNot(board, COMPLETE);

        for (TradeStatus tradeStatus : tradeStatuses) {
            updateStatus(tradeStatus, CANCELED);
        }
    }
}
