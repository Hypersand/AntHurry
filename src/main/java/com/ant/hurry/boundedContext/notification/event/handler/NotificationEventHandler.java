package com.ant.hurry.boundedContext.notification.event.handler;


import com.ant.hurry.boundedContext.notification.event.NotifyCancelMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyEndMessageEvent;
import com.ant.hurry.boundedContext.notification.event.NotifyNewMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventHandler {


    @EventListener
    public void notifyNewMessageEventListener(NotifyNewMessageEvent event) {

        //sms 전송

    }

    @EventListener
    public void notifyEndMessageEventListener(NotifyEndMessageEvent event) {

        //sms 전송
    }

    @EventListener
    public void notifyCancelMessageEventListener(NotifyCancelMessageEvent event) {

        //sms 전송
    }




}
