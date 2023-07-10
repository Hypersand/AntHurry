package com.ant.hurry.boundedContext.adm.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.adm.service.AdmService;
import com.ant.hurry.boundedContext.coin.entity.Exchange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "AdmController" , description = "관리자에 대한 컨트롤러")
public class AdmController {

    private final AdmService admService;
    private final Rq rq;

    @Operation(summary = "환전 신청 조회", description = "유저들이 신청한 환전신청들을 조회합니다.")
    @GetMapping("/list")
    public String showApplyList(Model model){
        List<Exchange> applyList = admService.getApplyList();
        model.addAttribute("applyList", applyList);
        return "adm/home/applyList";
    }

    @Operation(summary = "환전 신청 수락", description = "환전신청을 완료한 신청들을 수락합니다.")
    @PostMapping("/complete/{id}")
    public String acceptApplication(@PathVariable Long id){
        RsData rsData = admService.accept(id);
        if(rsData.isFail()){
            return rq.historyBack(rsData.getMsg());
        }
        return rq.redirectWithMsg("/adm/list", rsData.getMsg());
    }
}
