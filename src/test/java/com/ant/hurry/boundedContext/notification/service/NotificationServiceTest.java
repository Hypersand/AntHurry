package com.ant.hurry.boundedContext.notification.service;


import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.repository.MemberRepository;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import com.ant.hurry.boundedContext.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NotificationServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private Rq rq;

    @InjectMocks
    private NotificationService notificationService;

    @Autowired
    private MemberRepository memberRepository;

    private Member requester;
    private Member helper;

    @BeforeEach
    public void setMember() {
        requester = memberRepository.findByUsername("user1").orElse(null);
        helper = memberRepository.findByUsername("user2").orElse(null);
    }


    @Test
    @DisplayName("채팅을 시작하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyNew() {

        //given
        Notification notification = new Notification();
        when(memberService.findByUsername(anyString())).thenReturn(Optional.of(requester));
        when(rq.getMember()).thenReturn(helper);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        //when
        notificationService.notifyNew(requester, helper);

        //then
        verify(publisher, times(1)).publishEvent(any(NotifyNewMessageEvent.class));
    }

    @Test
    @DisplayName("거래를 완료하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyEnd() {

        //given
        Notification notification = new Notification();
        when(memberService.findByUsername(anyString())).thenReturn(Optional.of(requester));
        when(rq.getMember()).thenReturn(requester);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        //when
        notificationService.notifyEnd(requester, helper);

        //then
        verify(publisher, times(1)).publishEvent(any(NotifyEndMessageEvent.class));


    }

    @Test
    @DisplayName("거래를 파기하면 알림 엔티티가 생성되고 알림 이벤트가 발생한다.")
    void notifyCancel() {

        //given
        Notification notification = new Notification();
        when(memberService.findByUsername(anyString())).thenReturn(Optional.of(requester));
        when(rq.getMember()).thenReturn(requester);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        //when
        notification = notificationService.notifyCancel(requester, helper);

        //then
        verify(publisher, times(1)).publishEvent(any(NotifyCancelMessageEvent.class));
    }


    @Test
    @DisplayName("멤버가 null이면 알림 목록을 조회할 수 없다.")
    void findNotificationList_MemberNotExists() {

        //given
        String username = "not_exist_member";


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

        // given
        String message = "첫번째알림메시지";
        String type = "START";

        Notification notification = Notification.create(message, type, requester, helper);
        when(notificationRepository.findAllByMemberId(anyLong())).thenReturn(Collections.singletonList(notification));
        when(memberService.findByUsername(anyString())).thenReturn(Optional.of(requester));

        // when
        RsData<List<Notification>> rsData = notificationService.findNotificationList(requester.getUsername());

        // then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_N-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("알림목록페이지로 이동합니다."),
                () -> assertThat(rsData.getData().get(0).getMessage()).isEqualTo("첫번째알림메시지")
        );
    }

    @Test
    @DisplayName("알림 삭제 요청")
    void notification_delete() {

        // given
        when(memberService.findByUsername(anyString())).thenReturn(Optional.of(requester));

        // when
        RsData<Notification> rsData = notificationService.delete(requester.getId(), requester.getUsername());

        // then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_N-2"),
                () -> assertThat(rsData.getMsg()).isEqualTo("성공적으로 알림이 삭제되었습니다.")
        );
    }

}