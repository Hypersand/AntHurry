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
    public void notifyNew(String requesterPhoneNumber) {

        //알림 엔티티 생성

        //채팅시작알림 이벤트 발생 (title, content 수정 필요)
        publisher.publishEvent(new NotifyNewMessageEvent(requesterPhoneNumber,"",""));

    }


    //거래 완료
    public void notifyEnd(String requesterPhoneNumber, String helperPhoneNumber) {

        //알림 엔티티 생성

        //거래 완료 알림 이벤트 발생 (title, content 수정 필요)
        publisher.publishEvent(new NotifyEndMessageEvent(requesterPhoneNumber, helperPhoneNumber, "", ""));

    }


    //거래 파기
    public void notifyCancel(String requesterPhoneNumber, String helperPhoneNumber) {

        //알림 엔티티 생성

        //거래 파기 알림 이벤트 발생 (title, content 수정 필요)
        publisher.publishEvent(new NotifyCancelMessageEvent(requesterPhoneNumber, helperPhoneNumber, "", ""));

    }



}
