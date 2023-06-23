package com.ant.hurry.boundedContext.notification.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final Rq rq;


    //알림목록 페이지
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String list(@AuthenticationPrincipal User user, Model model) {

        RsData<List<Notification>> notificationListRsData = notificationService.findNotificationList(user.getUsername());

        model.addAttribute("notificationList", notificationListRsData.getData());

        //레이아웃 확인 데이터 생성
//        Member member1 = Member.create("test1", "nickname1", "123", "01012345678", "kakao");
//        Member member2= Member.create("test2", "nickname2", "123", "01034124123", "kakao");
//        List<Notification> list = Stream.of(
//                Notification.create("임시테스트1", "START", member1, member2),
//                Notification.create("임시테스트2", "END", member1, member2),
//                Notification.create("임시테스트3", "CANCEL", member1, member2),
//                Notification.create("임시테스트4", "START", member1, member2),
//                Notification.create("임시테스트5", "END", member1, member2),
//                Notification.create("임시테스트6", "CANCEL", member1, member2)
//        ).collect(Collectors.toList());


//        model.addAttribute("notificationList", list);

        return "notification/list";
    }





}
