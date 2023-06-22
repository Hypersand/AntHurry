package com.ant.hurry.boundedContext.notification.service;


import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.*;



@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RecordApplicationEvents
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    ApplicationEvents events;

    private Member requester;
    private Member helper;

    @BeforeEach
    public void setMember() {
        requester = new Member("손승완", "큰모래", "123", "01012345678", "kakao", 0);
        helper = new Member("멋쟁이사자", "라이크라이온", "123", "01056781234", "kakao", 0);
    }


    @Test
    @DisplayName("채팅을 시작하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyNew() {

        //when
        Notification notification = notificationService.notifyNew(requester, helper);

        //then
        long 채팅시작_알림_이벤트_카운트 = events.stream(NotifyNewMessageEvent.class).count();
        long 거래종료_알림_이벤트_카운트 = events.stream(NotifyEndMessageEvent.class).count();
        long 거래파기_알림_이벤트_카운트 = events.stream(NotifyCancelMessageEvent.class).count();

        assertThat(채팅시작_알림_이벤트_카운트).isEqualTo(1L);
        assertThat(거래종료_알림_이벤트_카운트).isEqualTo(0L);
        assertThat(거래파기_알림_이벤트_카운트).isEqualTo(0L);

        assertThat(notification.getMessage()).isEqualTo("채팅시작테스트");
        assertThat(notification.getHelper()).isEqualTo(helper);
        assertThat(notification.getRequester()).isEqualTo(requester);

    }

    @Test
    @DisplayName("거래를 종료하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyEnd() {

        //when
        Notification notification = notificationService.notifyEnd(requester, helper);

        //then
        long 채팅시작_알림_이벤트_카운트 = events.stream(NotifyNewMessageEvent.class).count();
        long 거래종료_알림_이벤트_카운트 = events.stream(NotifyEndMessageEvent.class).count();
        long 거래파기_알림_이벤트_카운트 = events.stream(NotifyCancelMessageEvent.class).count();

        assertThat(채팅시작_알림_이벤트_카운트).isEqualTo(0L);
        assertThat(거래종료_알림_이벤트_카운트).isEqualTo(1L);
        assertThat(거래파기_알림_이벤트_카운트).isEqualTo(0L);

        assertThat(notification.getMessage()).isEqualTo("거래완료테스트");
        assertThat(notification.getHelper()).isEqualTo(helper);
        assertThat(notification.getRequester()).isEqualTo(requester);

    }

    @Test
    @DisplayName("거래를 파기하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyCancel() {

        //when
        Notification notification = notificationService.notifyCancel(requester, helper);

        //then
        long 채팅시작_알림_이벤트_카운트 = events.stream(NotifyNewMessageEvent.class).count();
        long 거래종료_알림_이벤트_카운트 = events.stream(NotifyEndMessageEvent.class).count();
        long 거래파기_알림_이벤트_카운트 = events.stream(NotifyCancelMessageEvent.class).count();

        assertThat(채팅시작_알림_이벤트_카운트).isEqualTo(0L);
        assertThat(거래종료_알림_이벤트_카운트).isEqualTo(0L);
        assertThat(거래파기_알림_이벤트_카운트).isEqualTo(1L);

        assertThat(notification.getMessage()).isEqualTo("거래파기테스트");
        assertThat(notification.getHelper()).isEqualTo(helper);
        assertThat(notification.getRequester()).isEqualTo(requester);

    }

}