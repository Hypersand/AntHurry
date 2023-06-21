package com.ant.hurry.boundedContext.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {


    //알림목록 페이지
    public String list() {

        return "notification/list";
    }





}
