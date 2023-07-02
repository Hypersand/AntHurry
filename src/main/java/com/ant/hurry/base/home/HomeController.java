package com.ant.hurry.base.home;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import com.ant.hurry.boundedContext.board.service.BoardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final Rq rq;
    private final BoardService boardService;

    @GetMapping("/")
    public String showMain() {
        return "usr/home/main";
    }

}
