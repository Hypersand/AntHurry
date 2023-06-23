package com.ant.hurry.boundedContext.member.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.PhoneAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

    private final Rq rq;

    private final PhoneAuthService smsService;


    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin(){
        return "usr/member/login";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/edit")
    public String showProfile(Model model) {
        if (!rq.isLogin()) {
            return "redirect:/usr/member/login";
        }
        Member member = rq.getMember();
        model.addAttribute("member", member);

        return "usr/member/profile_edit";
    }


    @PostMapping("/phoneAuth")
    @ResponseBody
    public String phoneAuth(String phoneNumber) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        String authCode = smsService.sendSms(phoneNumber);

        return authCode;
    }
}
