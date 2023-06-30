package com.ant.hurry.base.home;

import com.ant.hurry.base.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final Rq rq;

    @GetMapping("/")
    public String showMain() {
        return "usr/home/main";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/")
    public String searchOnlineBoard() {
        return rq.redirectWithMsg("/board/online", "온라인 게시판으로 이동합니다.");
    }
}
