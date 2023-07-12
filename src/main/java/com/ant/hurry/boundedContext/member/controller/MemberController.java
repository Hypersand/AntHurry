package com.ant.hurry.boundedContext.member.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.BoardDto;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.service.BoardService;
import com.ant.hurry.boundedContext.member.dto.ProfileRequestDto;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.member.service.PhoneAuthService;
import com.ant.hurry.boundedContext.review.service.ReviewService;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import com.ant.hurry.chat.service.ChatRoomService;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
@Tag(name = "MemberController" , description = "유저에 관한 컨트롤러")
public class MemberController {
    private final MemberService memberService;
    private final PhoneAuthService phoneAuthService;
    private final TradeStatusService tradeStatusService;
    private final ReviewService reviewService;
    private final ChatRoomService chatRoomService;
    private final BoardService boardService;
    private final Rq rq;

    private final ObjectMapper objectMapper;

    @Operation(summary = "로그인", description = "로그인 페이지를 조회합니다.")
    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "usr/member/login";
    }

    @Operation(summary = "프로필 조회", description = "유저의 프로필을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Model model) {
        if (!rq.isLogin()) {
            return "redirect:/usr/member/login";
        }
        Member member = rq.getMember();
        Optional<ProfileImage> profileImage = memberService.findProfileImage(member);
        Long tradeStatusCount = tradeStatusService.getMemberTradeStatusCount(member.getId());
        Long reviewCount = reviewService.getMyReviewCount(member.getId());
        Long myTradeStatusCount = tradeStatusCount - reviewCount;
        if(myTradeStatusCount < 0){
            myTradeStatusCount = 0L;
        }
        model.addAttribute("member", member);
        model.addAttribute("profileImage", profileImage.orElse(null));
        model.addAttribute("myTradeStatusCount", myTradeStatusCount);
        return "usr/member/profile";
    }

    @Operation(summary = "휴대폰 인증", description = "휴대폰 인증페이지를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/phoneAuth")
    public String phoneAuthPage() {
        return "usr/member/phone_auth";
    }

    @Operation(summary = "휴대폰 인증 수행", description = "휴대폰 인증을 수행합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phoneAuth")
    @ResponseBody
    public ResponseEntity phoneAuthComplete(String phoneNumber) {
        Member member = rq.getMember();
        RsData<String> result = memberService.phoneAuthComplete(member, phoneNumber);
        if (result.isFail()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"" + result.getMsg() + "\"}");
        }
        memberService.updatePhoneNumber(member, phoneNumber);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\": \"" + result.getMsg() + "\"}");
    }

    @Operation(summary = "인증 번호 발신", description = "휴대폰 인증번호를 발신합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/phoneAuth/send", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity phoneAuth(String phoneNumber) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        //핸드폰 번호를 가지고 있는 사용자가 존재하는지 체크
        boolean existPhoneNumber = memberService.existsPhoneNumber(phoneNumber);
        if(existPhoneNumber){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"전화번호 중복\"}");
        }

        //SMS 인증번호 전송
        String authCode = phoneAuthService.sendSms(phoneNumber);

        Member member = rq.getMember();
        if (member != null) {
            memberService.updateTmpPhone(member, phoneNumber);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\":\"인증번호 전송 성공\"}");
    }

    @Operation(summary = "인증번호 체크", description = "휴대폰 인증번호가 올바른지 체크합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phoneAuth/check")
    @ResponseBody
    public ResponseEntity checkAuthCode(@RequestParam String phoneNumber, @RequestParam String authCode) {

        String storedAuthCode = phoneAuthService.getAuthCode(phoneNumber);

        if (storedAuthCode == null) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"인증번호가 만료되었습니다.\"}");
        }

        if (!storedAuthCode.equals(authCode)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"인증번호가 일치하지 않습니다.\"}");
        }

        //인증 성공 경우
        Member member = rq.getMember();
        if (member != null) {
            memberService.updatePhoneAuth(member);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\":\"인증 성공\"}");
    }

    @Operation(summary = "프로필 수정", description = "유저의 닉네임과 프로필사진을 수정하는 페이지를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile_edit")
    public String showProfileEdit(Model model) {
        if (!rq.isLogin()) {
            return "redirect:/usr/member/login";
        }
        Member member = rq.getMember();
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setNickname(member.getNickname());

        //이미지 경로 세팅
        Optional<ProfileImage> profileImage = memberService.findProfileImage(member);
        if (profileImage.isPresent())
            profileRequestDto.setImagePath(profileImage.get().getFullPath());
        else
            profileRequestDto.setImagePath(null);
        model.addAttribute("profileRequestDto", profileRequestDto);

        return "usr/member/profile_edit";
    }


    @Operation(summary = "프로필 사진 수정", description = "유저의 프로필 사진을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/profile_edit", produces = "application/json;utf-8;")
    @ResponseBody
    public ResponseEntity updateProfile(@ModelAttribute @Valid ProfileRequestDto profileRequestDto,
                                        BindingResult result,
                                        MultipartFile file) throws IOException {
        //입력필드 검증
        Map<String, String> errors = new HashMap<>();
        if (result.hasErrors()) {
            result.getFieldErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return new ResponseEntity<>(errors, HttpStatusCode.valueOf(HTTPResponse.SC_BAD_REQUEST));
        }
        Member member = rq.getMember();
        memberService.updateProfile(member, profileRequestDto.getNickname(), file);

        chatRoomService.updateMember(member);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\":\"성공\"}");
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/charge")
    public String chargePoint(Model model) {
        Member member = memberService.getMember();
        model.addAttribute("member", member);
        return "usr/member/charge";
    }


    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "usr/member/fail";

    }

    @Operation(summary = "상대방 프로필 조회", description = "상대방의 정보를 조회합니다.")
    @GetMapping("/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showProfile(@PathVariable Long id, Model model) {

        RsData<Member> rsData = memberService.validateAndReturnMember(id);

        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }

        Long completeTradeCount = tradeStatusService.getCompleteTradeStatusCount(id);
        Long reviewCount = reviewService.getReviewCount(id);
        Optional<ProfileImage> profileImage = memberService.findProfileImage(rsData.getData());
        RsData<Map<String, Object>> reviews = reviewService.getReviews(rsData.getData().getUsername(), rsData.getData().getId());
        model.addAttribute("reviewData", reviews.getData());
        model.addAttribute("profileImage", profileImage.orElse(null));
        model.addAttribute("member", rsData.getData());
        model.addAttribute("completeTradeCount", completeTradeCount);
        model.addAttribute("reviewCount", reviewCount);

        return "usr/member/usrCheck";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myboard")
    @Operation(summary = "내가 쓴 글", description = "내가 쓴 글을 보여줍니다.")
    public String showMyBoard(Model model, @RequestParam(value = "lastId", required = false) Long lastId) {
        Member member = rq.getMember();
        Slice<BoardDto> boards = boardService.getMyBoards(lastId, member, PageRequest.ofSize(10));
        model.addAttribute("boards", boards);
        return "usr/member/myboard";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myboard/{lastId}")
    @ResponseBody
    @Operation(summary = "내가 쓴 글 첫 페이지 이후", description = "내가 쓴 글을 첫 페이지 이후 페이지를 요청합니다.")
    public ResponseEntity<?> showMyBoard(@PathVariable("lastId") Long lastId) {
        Member member = rq.getMember();
        Slice<BoardDto> boards = boardService.getMyBoards(lastId, member, PageRequest.ofSize(10));
        Map<String, Object> map = new HashMap<>();
        map.put("boardList", boards.getContent());
        return ResponseEntity.ok(map);
    }

    @Operation(summary = "나급해요 공지사항", description = "나급해요 공지사항을 보여줍니다.")
    @GetMapping("/faq")
    public String showFaq() {
        return "usr/member/faq";
    }

}
