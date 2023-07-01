package com.ant.hurry.boundedContext.tradeStatus.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.service.BoardService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import com.ant.hurry.chat.controller.ChatRoomController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trade")
public class TradeStatusController {

    private final ChatRoomController chatRoomController;
    private final TradeStatusService tradeStatusService;
    private final BoardService boardService;
    private final Rq rq;

    @GetMapping("/create/{id}")
    public String create(@PathVariable Long id) {
        Optional<Board> op = boardService.findById(id);

        if(op.isEmpty()) {
            return rq.historyBack("존재하지 않는 게시물입니다.");
        }

        Board board = op.get();

        Member requester;
        Member helper;
        if(board.getMember().equals(rq.getMember())) {
            requester = rq.getMember();
            helper = board.getMember();
        } else {
            requester = board.getMember();
            helper = rq.getMember();
        }

        TradeStatus tradeStatus = tradeStatusService.create(board, requester, helper);

        return chatRoomController.create(tradeStatus);
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(@RequestParam(defaultValue = "COMPLETE") String status, @AuthenticationPrincipal User user, Model model) {

        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(user.getUsername(), Status.valueOf(status));

        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }

        model.addAttribute("tradeStatusList", rsData.getData());

        return "tradeStatus/list";
    }

    @GetMapping("/list/select")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> showListByResponseBody(@RequestParam(defaultValue = "COMPLETE") String status, @AuthenticationPrincipal User user, Model model) {

        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(user.getUsername(), Status.valueOf(status));

        Map<String, Object> map = new HashMap<>();

        map.put("statusMsg", Status.valueOf(status).msg);
        map.put("tradeStatusList", rsData.getData());
        return ResponseEntity.ok(map);
    }

}
