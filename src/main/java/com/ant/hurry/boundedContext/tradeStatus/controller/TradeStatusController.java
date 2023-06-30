package com.ant.hurry.boundedContext.tradeStatus.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.service.BoardService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trade")
public class TradeStatusController {

    private final TradeStatusService tradeStatusService;
    private final BoardService boardService;
    private final MemberService memberService;
    private final Rq rq;

//    @GetMapping("/create/{id}")
//    public String create(@PathVariable Long id) {
//        Board board = boardService.findById(id);
//        Member requester = board.getMember();
//        Member helper = rq.getMember();
//
//        tradeStatusService.create(board, requester, helper);
//        [event -> ChatRoom 생성]
//
//        return "[redirect ChatRoom]";
//    }

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

        if (status.equals("undefined")) {
            status = "COMPLETE";
        }

        RsData<List<TradeStatus>> rsData = tradeStatusService.findMyTradeStatusList(user.getUsername(), Status.valueOf(status));

        Map<String, Object> map = new HashMap<>();

        map.put("statusMsg", Status.valueOf(status).msg);
        map.put("tradeStatusList", rsData.getData());
        return ResponseEntity.ok(map);
    }

}
