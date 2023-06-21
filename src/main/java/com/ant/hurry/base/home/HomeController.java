package com.ant.hurry.base.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/main")
    public String showMain() {
        return "home/main";
    }
}
