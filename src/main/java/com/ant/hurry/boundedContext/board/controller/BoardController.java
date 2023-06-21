package com.ant.hurry.boundedContext.board.controller;

import com.ant.hurry.boundedContext.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createBoard(){
        return "board/create";
    }

}
