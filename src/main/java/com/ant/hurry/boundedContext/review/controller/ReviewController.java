package com.ant.hurry.boundedContext.review.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
@Slf4j
@Tag(name = "ReviewController", description = "리뷰에 관한 컨트롤러")
public class ReviewController {

    private final ReviewService reviewService;
    private final Rq rq;

    @Operation(summary = "리뷰 작성", description = "리뷰 작성 페이지를 조회합니다.")
    @GetMapping("/create/{tradeStatusId}")
    @PreAuthorize("isAuthenticated()")
    public String showCreate(Model model, @PathVariable Long tradeStatusId,
                             @AuthenticationPrincipal User user) {

        RsData<Object> rsData = reviewService.validateTradeStatusAndMember(tradeStatusId, user.getUsername());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.updateReceiverName((String) rsData.getData());
        model.addAttribute("reviewRequest", reviewRequest);
        model.addAttribute("tradeStatusId", tradeStatusId);
        return "review/create";
    }

    @Operation(summary = "리뷰 작성", description = "리뷰를 작성합니다.")
    @PostMapping("/create/{tradeStatusId}")
    @PreAuthorize("isAuthenticated()")
    public String create(@AuthenticationPrincipal User user ,@Valid @ModelAttribute ReviewRequest reviewRequest,
                         BindingResult bindingResult, @PathVariable Long tradeStatusId) {

        if (bindingResult.hasErrors()) {
            return rq.historyBack(bindingResult.getFieldError().getDefaultMessage());
        }

        log.info("받는사람= {}", reviewRequest.getReceiverName());

        RsData<Review> reviewRsData = reviewService.save(reviewRequest, user.getUsername(), tradeStatusId);

        return rq.redirectWithMsg("/review/list", reviewRsData);
    }

    @Operation(summary = "리뷰 목록", description = "유저의 리뷰들을 조회합니다.")
    @GetMapping("/list/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public String list(Model model, @AuthenticationPrincipal User user, @PathVariable Long memberId) {

        RsData<Map<String, Object>> rsData = reviewService.getReviews(user.getUsername(), memberId);

        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }

        model.addAttribute("reviewData", rsData.getData());

        return "review/list";
    }
}
