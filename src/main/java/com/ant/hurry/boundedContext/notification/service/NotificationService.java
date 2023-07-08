package com.ant.hurry.boundedContext.notification.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyStartMessageEvent;
import com.ant.hurry.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final ApplicationEventPublisher publisher;

    private final NotificationRepository notificationRepository;

    private final MemberService memberService;

    private final Rq rq;

    //채팅 시작
    public Notification notifyNew(Member requester, Member helper) {

        //메시지 내용 생성
        Member member = rq.getMember();
        String message;

        if (member.equals(requester)) {
            message = helper.getNickname() + "님 과의 채팅이 시작되었습니다.";
        }

        else {
            message = requester.getNickname() + "님 과의 채팅이 시작되었습니다.";
        }


        //알림 엔티티 생성
        Notification notification = Notification.create(message, "START", requester, helper);
        notificationRepository.save(notification);

        //채팅시작알림 이벤트 발생 (content 수정 필요)
        publisher.publishEvent(new NotifyNewMessageEvent(requester.getPhoneNumber(), message));

        return notification;
    }

    //거래 시작
    public Notification notifyStart(Member requester, Member helper) {

        //메시지 내용 생성
        Member member = rq.getMember();
        String message;

        if (member.equals(requester)) {
            message = helper.getNickname() + "님 과의 거래가 시작되었습니다.";
        }

        else {
            message = requester.getNickname() + "님 과의 거래가 시작되었습니다.";
        }


        //알림 엔티티 생성
        Notification notification = Notification.create(message, "INPROGRESS", requester, helper);
        notificationRepository.save(notification);

        //채팅시작알림 이벤트 발생 (content 수정 필요)
        publisher.publishEvent(new NotifyStartMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), message));

        return notification;
    }


    //거래 완료
    public Notification notifyEnd(Member requester, Member helper) {

        //메시지 내용 생성
        Member member = rq.getMember();
        String message;

        if (member.equals(requester)) {
            message = helper.getNickname() + "님 과의 거래가 종료되었습니다.";
        }

        else {
            message = requester.getNickname() + "님 과의 거래가 종료되었습니다.";
        }

        //알림 엔티티 생성
        Notification notification = Notification.create(message, "END", requester, helper);
        notificationRepository.save(notification);

        //거래 완료 알림 이벤트 발생 (content 수정 필요)
        publisher.publishEvent(new NotifyEndMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), message));

        return notification;
    }


    //거래 파기
    public Notification notifyCancel(Member requester, Member helper) {

        //메시지 내용 생성
        Member member = rq.getMember();
        String message;

        if (member.equals(requester)) {
            message = helper.getNickname() + "님 과의 거래가 취소되었습니다.";
        }

        else {
            message = requester.getNickname() + "님 과의 거래가 취소되었습니다.";
        }

        //알림 엔티티 생성
        Notification notification = Notification.create(message, "CANCEL", requester, helper);

        notificationRepository.save(notification);

        //거래 파기 알림 이벤트 발생 (title, content 수정 필요)
        publisher.publishEvent(new NotifyCancelMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), message));

        return notification;
    }

    @Transactional(readOnly = true)
    public RsData<List<Notification>> findNotificationList(String username) {

        Member member = memberService.findByUsername(username).orElse(null);

        if (member == null) {
            //MEMBER_NOT_EXISTS
            return RsData.of("F_M-1", "존재하지 않는 회원입니다.");
        }


        //NOTIFICATION_LIST_MOVE
        return RsData.of("S_N-1", "알림목록페이지로 이동합니다.",
                notificationRepository.findAllByMemberId(member.getId()));
    }

    public RsData<Notification> delete(Long id, String username) {

        Member member = memberService.findByUsername(username).orElse(null);

        if (member == null) {
            return RsData.of("F_M-1", "존재하지 않는 회원입니다.");
        }


        notificationRepository.deleteById(id);

        return RsData.of("S_N-2", "성공적으로 알림이 삭제되었습니다.");
    }


}
