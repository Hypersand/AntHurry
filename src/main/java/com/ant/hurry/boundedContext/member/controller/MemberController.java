package com.ant.hurry.boundedContext.member.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.dto.ProfileRequestDto;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.member.service.PhoneAuthService;
import jakarta.validation.Valid;
import com.ant.hurry.standard.util.Ut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {
    private final MemberService memberService;
    private final PhoneAuthService phoneAuthService;
    private final Rq rq;

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
        Optional<ProfileImage> profileImage = memberService.findProfileImage(member);
        model.addAttribute("profileImage", profileImage.orElse(null));
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
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setNickname(member.getNickname());
        model.addAttribute("profileRequestDto", profileRequestDto);

        return "usr/member/profile_edit";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile_edit")
    public String updateProfile(@ModelAttribute @Valid ProfileRequestDto profileRequestDto, BindingResult bindingResult,
                                MultipartFile file) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/usr/member/profile_edit";
        }
        Member member = rq.getMember();
        memberService.updateProfile(member, profileRequestDto.getNickname(), file);

        return "redirect:/usr/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/charge")
    public String chargePoint(Model model){
        Member member = memberService.getMember();
        model.addAttribute("member", member);
        return "usr/member/charge";
    }

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }


    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable Long id,
            @RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Long amount,
            Model model) throws Exception {

        RsData rsData = memberService.checkCanCharge(id, orderId);

        if(rsData.isFail()){
            return rq.redirectWithMsg("/usr/member/charge", rsData);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode successNode = responseEntity.getBody();
            model.addAttribute("orderId", successNode.get("orderId").asText());
            memberService.addCoin(memberService.getMember(), amount, "코인충전");
            return "redirect:/usr/member/profile?msg=%s".formatted(Ut.url.encode(rsData.getMsg()));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "usr/member/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "usr/member/fail";

    }

}
