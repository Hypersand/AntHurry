package com.ant.hurry.boundedContext.member.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.member.service.PhoneAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

    private final Rq rq;

    private final PhoneAuthService phoneAuthService;

    private final MemberService memberService;


    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "usr/member/login";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Model model) {
        if (!rq.isLogin()) {
            return "redirect:/usr/member/login";
        }
        Member member = rq.getMember();
        model.addAttribute("member", member);

        return "usr/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/phoneAuth")
    public String phoneAuthPage() {
        return "usr/member/phone_auth";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phoneAuth")
    @ResponseBody
    public ResponseEntity phoneAuthComplete(String phoneNumber){
        Member member = rq.getMember();
        RsData<?> result = memberService.phoneAuthComplete(member, phoneNumber);
        if (result.isFail()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getMsg());
        }
        memberService.updatePhoneNumber(member, phoneNumber);
        return ResponseEntity.status(HttpStatus.OK).body(result.getMsg());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phoneAuth/send")
    @ResponseBody
    public ResponseEntity phoneAuth(String phoneNumber) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        //핸드폰 번호를 가지고 있는 사용자가 존재하는지 체크
        boolean existPhoneNumber = memberService.existsPhoneNumber(phoneNumber);
        if(existPhoneNumber){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전화번호 중복");
        }

        //SMS 인증번호 전송
        String authCode = phoneAuthService.sendSms(phoneNumber);

        Member member = rq.getMember();
        if(member != null){
            memberService.updateTmpPhone(member, phoneNumber);
        }

        return ResponseEntity.status(HttpStatus.OK).body("인증번호 전송 성공");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phoneAuth/check")
    @ResponseBody
    public ResponseEntity checkAuthCode(@RequestParam String phoneNumber, @RequestParam String authCode) {

        String storedAuthCode = phoneAuthService.getAuthCode(phoneNumber);

        if (storedAuthCode == null) {
            return ResponseEntity.status(HttpStatus.GONE).body("인증번호가 만료되었습니다.");
        }

        if (!storedAuthCode.equals(authCode)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증번호가 일치하지 않습니다.");
        }

        //인증 성공 경우
        Member member = rq.getMember();
        if (member != null) {
            memberService.updatePhoneAuth(member);
        }
        return ResponseEntity.status(HttpStatus.OK).body("인증 성공");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile_edit")
    public String showProfileEdit(Model model) {
        if (!rq.isLogin()) {
            return "redirect:/usr/member/login";
        }
        Member member = rq.getMember();
        model.addAttribute("member", member);

        return "usr/member/profile_edit";
    }
}
