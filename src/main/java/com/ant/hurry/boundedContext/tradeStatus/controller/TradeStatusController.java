package com.ant.hurry.boundedContext.tradeStatus.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.service.BoardService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.notification.service.NotificationService;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ant.hurry.boundedContext.tradeStatus.entity.Status.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/trade")
@Tag(name = "TradeStatusController", description = "거래 상태 전반에 관한 컨트롤러")
public class TradeStatusController {

    private final ChatRoomService chatRoomService;
    private final TradeStatusService tradeStatusService;
    private final BoardService boardService;
    private final NotificationService notificationService;
    private final Rq rq;

    @Operation(summary = "거래 상태 생성", description = "게시물의 채팅하기 버튼을 누르면 거래 상태가 생성됩니다.")
    @GetMapping("/create/{id}")
    public String create(@PathVariable Long id) {

        Optional<Board> opBoard = boardService.findByIdWithMember(id);

        if (opBoard.isEmpty()) return rq.historyBack("존재하지 않는 게시물입니다.");
        Optional<TradeStatus> checkExistStatus = tradeStatusService.checkExistStatus(id, rq.getMember().getId());
        if (checkExistStatus.isPresent()) {
            RsData<ChatRoom> chatRoomRsData = chatRoomService.findByTradeStatusId(checkExistStatus.get().getId());
            return "redirect:/chat/room/%s".formatted(chatRoomRsData.getData().getId());
        }
        Board board = opBoard.get();
        Member requester;
        Member helper;
        if (board.getMember().equals(rq.getMember())) {
            requester = rq.getMember();
            helper = board.getMember();
        } else {
            requester = board.getMember();
            helper = rq.getMember();
        }

        RsData<TradeStatus> tradeStatus = tradeStatusService.create(board, requester, helper);
        RsData<ChatRoom> chatRoom = chatRoomService.create(tradeStatus.getData());
        notificationService.notifyNew(requester, helper);
        return "redirect:/chat/room/%s".formatted(chatRoom.getData().getId());
    }

    @Operation(summary = "거래 상태 목록 조회", description = "유저의 현재 거래 상태 목록을 조회합니다.")
    @GetMapping("/list")
    public String showList(@RequestParam(defaultValue = "COMPLETE") String status, @AuthenticationPrincipal User user, Model model) {

        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(user.getUsername(), valueOf(status));

        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }

        model.addAttribute("tradeStatusList", rsData.getData());

        return "tradeStatus/list";
    }

    @Operation(summary = "거래 상태 목록 조회", description = "유저의 현재 거래 상태 목록을 조회합니다.")
    @GetMapping("/list/select")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showListByResponseBody(@RequestParam(defaultValue = "COMPLETE") String status, @AuthenticationPrincipal User user, Model model) {

        if (status.equals("undefined")) {
            status = "COMPLETE";
        }

        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(user.getUsername(), valueOf(status));

        Map<String, Object> map = new HashMap<>();

        map.put("statusMsg", valueOf(status).msg);
        map.put("tradeStatusList", rsData.getData());
        return ResponseEntity.ok(map);
    }

    @Operation(summary = "거래 시작", description = "거래 상태를 진행 중으로 변경합니다.")
    @GetMapping("/start/{id}")
    public String start(@PathVariable Long id) {

        TradeStatus tradeStatus = tradeStatusService.findById(id).getData();
        RsData<TradeStatus> rs = tradeStatusService.updateStatus(tradeStatus, INPROGRESS);

        if (rs.isFail()) {
            return rq.historyBack(rs.getMsg());
        }

        ChatRoom chatRoom = chatRoomService.findByTradeStatusId(rs.getData().getId()).getData();
        notificationService.notifyStart(tradeStatus.getRequester(), tradeStatus.getHelper());
        return "redirect:/chat/room/%s".formatted(chatRoom.getId());
    }

    @Operation(summary = "거래 취소", description = "거래 상태를 취소로 변경합니다.")
    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {

        TradeStatus tradeStatus = tradeStatusService.findById(id).getData();
        RsData<TradeStatus> rs = tradeStatusService.updateStatus(tradeStatus, CANCELED);

        if (rs.isFail()) {
            return rq.historyBack(rs.getMsg());
        }

        ChatRoom chatRoom = chatRoomService.findByTradeStatusId(rs.getData().getId()).getData();
        notificationService.notifyCancel(tradeStatus.getRequester(), tradeStatus.getHelper());
        return "redirect:/chat/room/%s".formatted(chatRoom.getId());
    }

    @Operation(summary = "거래 완료", description = "거래 상태를 완료로 변경합니다.")
    @GetMapping("/complete/{id}")
    public String complete(@PathVariable Long id) {

        TradeStatus tradeStatus = tradeStatusService.findById(id).getData();
        RsData<TradeStatus> rs = tradeStatusService.updateStatus(tradeStatus, COMPLETE);

        if (rs.isFail()) {
            return rq.historyBack(rs.getMsg());
        }

        notificationService.notifyEnd(tradeStatus.getRequester(), tradeStatus.getHelper());
        return "redirect:/review/create/%d".formatted(id);
    }

}
