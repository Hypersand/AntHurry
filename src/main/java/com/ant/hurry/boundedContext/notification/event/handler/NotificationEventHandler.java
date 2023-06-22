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

        //sms 전송
        smsService.sendSms(event.getRequesterPhoneNumber(), event.getContent());

    }

    @EventListener
    public void notifyEndMessageEventListener(NotifyEndMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송
        smsService.sendSms(event.getRequesterPhoneNumber(), event.getContent());
        smsService.sendSms(event.getHelperPhoneNumber(), event.getContent());
    }

    @EventListener
    public void notifyCancelMessageEventListener(NotifyCancelMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송
        smsService.sendSms(event.getRequesterPhoneNumber(), event.getContent());
        smsService.sendSms(event.getHelperPhoneNumber(), event.getContent());
    }


}
