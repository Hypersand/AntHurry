package com.ant.hurry.boundedContext.notification.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyStartMessageEvent;
import com.ant.hurry.boundedContext.notification.repository.NotificationRepository;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ant.hurry.boundedContext.member.code.MemberErrorCode.MEMBER_NOT_EXISTS;
import static com.ant.hurry.boundedContext.notification.code.NotificationSuccessCode.*;

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
    public void notifyEnd(Member requester, Member helper, Long id) {

        String messageToRequester = helper.getNickname() + "님과의 거래가 종료되었습니다.";
        String messageToHelper = requester.getNickname() + "님과의 거래가 종료되었습니다.";

        Notification notificationToRequester = Notification.create(messageToRequester, "END", requester, null);
        Notification notificationToHelper = Notification.create(messageToHelper, "END", null, helper, id);
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
            return RsData.of(MEMBER_NOT_EXISTS);
        }


        //NOTIFICATION_LIST_MOVE
        return RsData.of(REDIRECT_NOTIFICATION_LIST_PAGE,
                notificationRepository.findAllByMemberId(member.getId()));
    }

    public RsData<Notification> delete(Long id, String username) {

        Member member = memberService.findByUsername(username).orElse(null);

        if (member == null) {
            return RsData.of(MEMBER_NOT_EXISTS);
        }


        notificationRepository.deleteById(id);

        return RsData.of(REMOVE_NOTIFICATION);
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
