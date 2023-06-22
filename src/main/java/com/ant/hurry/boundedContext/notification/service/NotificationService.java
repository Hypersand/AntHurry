package com.ant.hurry.boundedContext.notification.service;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import com.ant.hurry.boundedContext.notification.repository.NotificationRepository;
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

    private final NotificationRepository notificationRepository;

    //채팅 시작
    public Notification notifyNew(Member requester, Member helper) {

        //메시지 내용 생성
        String content = "채팅시작테스트";

        //알림 엔티티 생성
        Notification notification = Notification.create(content, requester, helper);
        notificationRepository.save(notification);

        //채팅시작알림 이벤트 발생 (content 수정 필요)
        publisher.publishEvent(new NotifyNewMessageEvent(requester.getPhoneNumber(), content));

        return notification;
    }


    //거래 완료
    public Notification notifyEnd(Member requester, Member helper) {

        //메시지 내용 생성
        String content = "거래완료테스트";

        //알림 엔티티 생성
        Notification notification = Notification.create(content, requester, helper);
        notificationRepository.save(notification);

        //거래 완료 알림 이벤트 발생 (content 수정 필요)
        publisher.publishEvent(new NotifyEndMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), content));

        return notification;
    }


    //거래 파기
    public Notification notifyCancel(Member requester, Member helper) {

        //메시지 내용 생성
        String content = "거래파기테스트";

        //알림 엔티티 생성
        Notification notification = Notification.create(content, requester, helper);
        notificationRepository.save(notification);

        //거래 파기 알림 이벤트 발생 (title, content 수정 필요)
        publisher.publishEvent(new NotifyCancelMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), content));

        return notification;
    }


}
