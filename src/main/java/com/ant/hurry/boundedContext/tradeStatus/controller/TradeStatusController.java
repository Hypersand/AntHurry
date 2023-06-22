package com.ant.hurry.boundedContext.tradeStatus.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.service.BoardService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trade")
public class TradeStatusController {

    private final TradeStatusService tradeStatusService;
    private final BoardService boardService;
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
}
