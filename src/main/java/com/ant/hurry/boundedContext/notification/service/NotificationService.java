package com.ant.hurry.boundedContext.notification.service;

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


    //채팅 시작
    public void notifyNew(Member requester, Member helper) {

        String messageToRequester = helper.getNickname() + "님과의 채팅이 시작되었습니다.";

        Notification notificationToRequester = Notification.create(messageToRequester, "START", requester, null);
        notificationRepository.save(notificationToRequester);

        publisher.publishEvent(new NotifyNewMessageEvent(requester.getPhoneNumber(), messageToRequester));
    }

    //거래 시작
    public void notifyStart(Member requester, Member helper) {

        String messageToRequester = helper.getNickname() + "님과의 거래가 시작되었습니다.";
        String messageToHelper = requester.getNickname() + "님과의 거래가 시작되었습니다.";

        Notification notificationToRequester = Notification.create(messageToRequester, "INPROGRESS", requester, null);
        Notification notificationToHelper = Notification.create(messageToHelper, "INPROGRESS", null, helper);
        notificationRepository.save(notificationToRequester);
        notificationRepository.save(notificationToHelper);

        publisher.publishEvent(new NotifyStartMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), messageToRequester, messageToHelper));
    }


    //거래 완료
    public void notifyEnd(Member requester, Member helper) {

        String messageToRequester = helper.getNickname() + "님과의 거래가 종료되었습니다.";
        String messageToHelper = requester.getNickname() + "님과의 거래가 종료되었습니다.";

        Notification notificationToRequester = Notification.create(messageToRequester, "END", requester, null);
        Notification notificationToHelper = Notification.create(messageToHelper, "END", null, helper);
        notificationRepository.save(notificationToRequester);
        notificationRepository.save(notificationToHelper);

        publisher.publishEvent(new NotifyEndMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), messageToRequester, messageToHelper));
    }


    //거래 취소
    public void notifyCancel(Member requester, Member helper) {

        String messageToRequester = helper.getNickname() + "님과의 거래가 취소되었습니다.";
        String messageToHelper = requester.getNickname() + "님과의 거래가 취소되었습니다.";

        Notification notificationToRequester = Notification.create(messageToRequester, "CANCEL", requester, null);
        Notification notificationToHelper = Notification.create(messageToHelper, "CANCEL", null, helper);
        notificationRepository.save(notificationToRequester);
        notificationRepository.save(notificationToHelper);

        publisher.publishEvent(new NotifyCancelMessageEvent(requester.getPhoneNumber(), helper.getPhoneNumber(), messageToRequester, messageToHelper));
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

    public void markRead(List<Notification> notifications) {
        notifications.stream()
                .filter(notification -> !notification.isRead())
                .forEach(Notification::markRead);
    }

    public boolean countUnreadNotifications(Member member) {

        return notificationRepository.countByMemberAndReadDateIsNull(member) > 0;
    }


}
