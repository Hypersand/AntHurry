package com.ant.hurry.boundedContext.review.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.review.service.ReviewService;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;
    private final TradeStatusService tradeStatusService;
    private final Rq rq;

    @GetMapping("/create/{tradeStatusId}")
    @PreAuthorize("isAuthenticated()")
    public String showCreate(Model model, @PathVariable Long tradeStatusId,
                             @AuthenticationPrincipal User user) {


        model.addAttribute("reviewRequest", new ReviewRequest());

        TradeStatus tradeStatus = tradeStatusService.findById(tradeStatusId);
        Member member = memberService.findByUsername(user.getUsername()).orElse(null);

        //TRADE_STATUS의 status가 COMPLETE일때만 리뷰를 남길 수 있다.
        if (!tradeStatus.getStatus().name().equals("COMPLETE")) {
            RsData<Object> rsData = RsData.of("F_T-2", "아직 리뷰를 남길 수 없는 거래입니다.");
            model.addAttribute("resultCode", "F_T-2");
            return rq.historyBack(rsData);
        }

        if (reviewService.isAlreadyReviewed(member, tradeStatus)) {
            RsData<Object> rsData = RsData.of("F_R-1", "이미 후기를 작성했습니다.");
            model.addAttribute("resultCode", "F_R-1");
            return rq.historyBack(rsData);
        }


        if (!member.getUsername().equals(tradeStatus.getRequesterUsername()) && !member.getUsername().equals(tradeStatus.getHelperUsername())) {
            RsData<Object> rsData = RsData.of("F_M-2", "접근할 수 있는 권한이 없습니다.");
            model.addAttribute("resultCode", "F_M-2");
            return rq.historyBack(rsData);
        }

        if (tradeStatus.getRequesterUsername().equals(member.getUsername())) {
            model.addAttribute("opponentNickname", tradeStatus.getHelper().getNickname());
            return "review/create";
        }

        model.addAttribute("opponentNickname", tradeStatus.getRequester().getNickname());

        return "review/create";
    }

    @PostMapping("/create/{tradeStatusId}")
    @PreAuthorize("isAuthenticated()")
    public String create(@Valid @ModelAttribute ReviewRequest reviewRequest,
                         BindingResult bindingResult, Model model,
                         @PathVariable Long tradeStatusId,
                         @AuthenticationPrincipal User user) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            return "review/create";
        }

        RsData<Review> reviewRsData = reviewService.save(reviewRequest, user.getUsername(), tradeStatusId);

        return rq.redirectWithMsg("/review/list", reviewRsData);
    }
}
