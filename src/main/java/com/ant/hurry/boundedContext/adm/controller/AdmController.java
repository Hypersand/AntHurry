package com.ant.hurry.boundedContext.adm.controller;

import com.ant.hurry.boundedContext.adm.service.AdmService;
import com.ant.hurry.boundedContext.coin.entity.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adm")
public class AdmController {

    private final AdmService admService;

    @GetMapping("/list")
    public String showApplyList(Model model){
        List<Exchange> applyList = admService.getApplyList();
        model.addAttribute("applyList", applyList);
        return "adm/home/applyList";
    }
}
