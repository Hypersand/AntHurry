package com.ant.hurry.boundedContext.review.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final Rq rq;

    @GetMapping("/create/{tradeStatusId}")
    @PreAuthorize("isAuthenticated()")
    public String showCreate(Model model, @PathVariable Long tradeStatusId,
                             @AuthenticationPrincipal User user) {

        model.addAttribute("reviewRequest", new ReviewRequest());

        RsData<Object> rsData = reviewService.validateTradeStatusAndMember(tradeStatusId, user.getUsername());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        model.addAttribute("opponentNickname", rsData.getData());

        return "review/create";
    }

    @PostMapping("/create/{tradeStatusId}")
    @PreAuthorize("isAuthenticated()")
    public String create(@ModelAttribute @Validated ReviewRequest reviewRequest,
                         BindingResult bindingResult, Model model,
                         @PathVariable Long tradeStatusId,
                         @AuthenticationPrincipal User user) {

        log.info("내용 = {}", reviewRequest.getContent());
        log.info("별점 = {}", reviewRequest.getRating());

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            return "review/create";
        }

        RsData<Review> reviewRsData = reviewService.save(reviewRequest, user.getUsername(), tradeStatusId);

        return rq.redirectWithMsg("/review/list", reviewRsData);
    }
}
