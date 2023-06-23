package com.ant.hurry.boundedContext.review.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.review.dto.ReviewRequest;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final Rq rq;

    @GetMapping("/create")
    public String showCreate(@ModelAttribute ReviewRequest reviewDto) {

        return "review/create";
    }

    @PostMapping("/create/{tradeStatusId}")
    public String create(@Valid @ModelAttribute ReviewRequest reviewRequest,
                         BindingResult bindingResult, Model model,
                         @PathVariable Long tradeStatusId,
                         @AuthenticationPrincipal User user)
    {

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            return "review/create";
        }

        RsData<Review> reviewRsData = reviewService.save(reviewRequest, user.getUsername(), tradeStatusId);

        return rq.redirectWithMsg("/review/list", reviewRsData);
    }
}
