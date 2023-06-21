package com.ant.hurry.boundedContext.notification.service;

import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final ApplicationEventPublisher publisher;


    //채팅 시작
    public void notifyNew() {

        //알림 엔티티 생성


        //채팅시작알림 이벤트 발생
        publisher.publishEvent(new NotifyNewMessageEvent());

    }



    //거래 완료
    public void notifyEnd() {

        //알림 엔티티 생성

        //거래 완료 알림 이벤트 발생
        publisher.publishEvent(new NotifyEndMessageEvent());

    }



    //거래 파기
    public void notifyCancel() {

        //알림 엔티티 생성


        //거래 파기 알림 이벤트 발생
        publisher.publishEvent(new NotifyCancelMessageEvent());

    }



}
