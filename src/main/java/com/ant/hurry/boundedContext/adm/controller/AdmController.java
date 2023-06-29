package com.ant.hurry.boundedContext.adm.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.adm.service.AdmService;
import com.ant.hurry.boundedContext.coin.entity.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adm")
@PreAuthorize("hasAuthority('admin')")
public class AdmController {

    private final AdmService admService;
    private final Rq rq;

    @GetMapping("/list")
    public String showApplyList(Model model){
        List<Exchange> applyList = admService.getApplyList();
        model.addAttribute("applyList", applyList);
        return "adm/home/applyList";
    }

    @PostMapping("/complete/{id}")
    public String acceptApplication(@PathVariable Long id){
        RsData rsData = admService.accept(id);
        if(rsData.isFail()){
            return rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/adm/list", rsData.getMsg());
    }
}
