package com.ant.hurry.boundedContext.notification.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "NotificationController", description = "알림에 관한 컨트롤러")
public class NotificationController {

    private final NotificationService notificationService;
    private final Rq rq;


    @Operation(summary = "알림 조회", description = "유저의 모든 알림들을 조회합니다.")
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String list(@AuthenticationPrincipal User user, Model model) {

        RsData<List<Notification>> notificationListRsData = notificationService.findNotificationList(user.getUsername());
        notificationService.markRead(notificationListRsData.getData());

        model.addAttribute("notificationList", notificationListRsData.getData());

        return "notification/list";
    }

    @Operation(summary = "알림 삭제", description = "알림을 삭제합니다.")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@AuthenticationPrincipal User user, @PathVariable Long id) {

        RsData<Notification> rsData = notificationService.delete(id, user.getUsername());

        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }


        return rq.redirectWithMsg("/notification/list", rsData.getMsg());
    }


}
