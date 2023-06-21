package com.ant.hurry.boundedContext.notification.event.handler;


import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import com.ant.hurry.boundedContext.sms.service.SmsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final SmsService smsService;


    @EventListener

    public void notifyNewMessageEventListener(NotifyNewMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송 (메시지 수정 필요)
        smsService.sendSms(event.getRequesterPhoneNumber(), "당신을 도와주실 분이 채팅을 원합니다.");

    }

    @EventListener
    public void notifyEndMessageEventListener(NotifyEndMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송 (메시지 수정 필요)
        smsService.sendSms(event.getRequesterPhoneNumber(), "거래가 완료되었습니다.");
        smsService.sendSms(event.getHelperPhoneNumber(), "거래가 완료되었습니다.");
    }

    @EventListener
    public void notifyCancelMessageEventListener(NotifyCancelMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송 (메시지 수정 필요)
        smsService.sendSms(event.getRequesterPhoneNumber(), "거래가 파기되었습니다.");
        smsService.sendSms(event.getHelperPhoneNumber(), "거래가 파기되었습니다.");
    }


}
