package com.ant.hurry.boundedContext.notification.event.handler;


import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyStartMessageEvent;
import com.ant.hurry.boundedContext.sms.service.SmsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class NotificationEventHandler {

    private final SmsService smsService;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void notifyNewMessageEventListener(NotifyNewMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송
        smsService.sendSms(event.getRequesterPhoneNumber(), event.getContent());

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void notifyStartMessageEventListener(NotifyStartMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송
        smsService.sendSms(event.getRequesterPhoneNumber(), event.getContentToRequester());
        smsService.sendSms(event.getHelperPhoneNumber(), event.getContentToHelper());

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void notifyEndMessageEventListener(NotifyEndMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송
        smsService.sendSms(event.getRequesterPhoneNumber(), event.getContentToRequester());
        smsService.sendSms(event.getHelperPhoneNumber(), event.getContentToHelper());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyCancelMessageEventListener(NotifyCancelMessageEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        //sms 전송
        smsService.sendSms(event.getRequesterPhoneNumber(), event.getContentToRequester());
        smsService.sendSms(event.getHelperPhoneNumber(), event.getContentToHelper());
    }


}
