package com.ant.hurry.boundedContext.notification.service;


import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;

import com.ant.hurry.boundedContext.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RecordApplicationEvents
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    ApplicationEvents events;

    @MockBean
    MemberService memberService;

    @MockBean
    NotificationRepository notificationRepository;

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
        long 거래완료_알림_이벤트_카운트 = events.stream(NotifyEndMessageEvent.class).count();
        long 거래파기_알림_이벤트_카운트 = events.stream(NotifyCancelMessageEvent.class).count();

        assertAll(
                () -> assertThat(채팅시작_알림_이벤트_카운트).isEqualTo(1L),
                () -> assertThat(거래완료_알림_이벤트_카운트).isEqualTo(0L),
                () -> assertThat(거래파기_알림_이벤트_카운트).isEqualTo(0L)
        );


        assertAll(
                () -> assertThat(notification.getMessage()).isEqualTo("채팅시작테스트"),
                () -> assertThat(notification.getHelper()).isEqualTo(helper),
                () -> assertThat(notification.getRequester()).isEqualTo(requester)
        );

    }

    @Test
    @DisplayName("거래를 완료하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyEnd() {

        //when
        Notification notification = notificationService.notifyEnd(requester, helper);

        //then
        long 채팅시작_알림_이벤트_카운트 = events.stream(NotifyNewMessageEvent.class).count();
        long 거래완료_알림_이벤트_카운트 = events.stream(NotifyEndMessageEvent.class).count();
        long 거래파기_알림_이벤트_카운트 = events.stream(NotifyCancelMessageEvent.class).count();

        assertAll(
                () -> assertThat(채팅시작_알림_이벤트_카운트).isEqualTo(0L),
                () -> assertThat(거래완료_알림_이벤트_카운트).isEqualTo(1L),
                () -> assertThat(거래파기_알림_이벤트_카운트).isEqualTo(0L)
        );


        assertAll(
                () -> assertThat(notification.getMessage()).isEqualTo("거래완료테스트"),
                () -> assertThat(notification.getHelper()).isEqualTo(helper),
                () -> assertThat(notification.getRequester()).isEqualTo(requester)
        );

    }

    @Test
    @DisplayName("거래를 파기하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyCancel() {

        //when
        Notification notification = notificationService.notifyCancel(requester, helper);

        //then
        long 채팅시작_알림_이벤트_카운트 = events.stream(NotifyNewMessageEvent.class).count();
        long 거래완료_알림_이벤트_카운트 = events.stream(NotifyEndMessageEvent.class).count();
        long 거래파기_알림_이벤트_카운트 = events.stream(NotifyCancelMessageEvent.class).count();

        assertAll(
                () -> assertThat(채팅시작_알림_이벤트_카운트).isEqualTo(0L),
                () -> assertThat(거래완료_알림_이벤트_카운트).isEqualTo(0L),
                () -> assertThat(거래파기_알림_이벤트_카운트).isEqualTo(1L)
        );


        assertAll(
                () -> assertThat(notification.getMessage()).isEqualTo("거래파기테스트"),
                () -> assertThat(notification.getHelper()).isEqualTo(helper),
                () -> assertThat(notification.getRequester()).isEqualTo(requester)
        );

    }


    @Test
    @DisplayName("멤버가 null이면 알림 목록을 조회할 수 없다.")
    void findNotificationList_MemberNotExists() {

        //given
        String username = "bigsand";

        when(memberService.findByUsername(username)).thenReturn(Optional.empty());

        //when
        RsData<List<Notification>> notificationRsData = notificationService.findNotificationList(username);


        //then
        assertAll(
                () -> assertThat(notificationRsData.getResultCode()).isEqualTo("F_M-1"),
                () -> assertThat(notificationRsData.getMsg()).isEqualTo("존재하지 않는 회원입니다.")
        );


    }


    @Test
    @DisplayName("멤버가 null이 아닐 때 알림 목록을 조회할 수 있다.")
    void findNotificationList_MemberExists() {

        //given
        String username = "손승완";
        String message = "첫번째알림메시지";

        when(memberService.findByUsername(username)).thenReturn(Optional.of(requester));
        when(notificationRepository.findAllByMemberId(requester.getId())).thenReturn(List.of(new Notification(message, requester, helper)));

        //when
        RsData<List<Notification>> notificationRsData = notificationService.findNotificationList(requester.getUsername());


        //then
        assertAll(
                () -> assertThat(notificationRsData.getResultCode()).isEqualTo("S_N-1"),
                () -> assertThat(notificationRsData.getMsg()).isEqualTo("알림목록페이지로 이동합니다."),
                () -> assertThat(notificationRsData.getData().size()).isEqualTo(1),
                () -> assertThat(notificationRsData.getData().get(0).getMessage()).isEqualTo("첫번째알림메시지")
        );



    }

}