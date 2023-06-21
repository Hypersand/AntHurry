package com.ant.hurry.boundedContext.notification.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotifyNewMessageEvent {

    private String requesterPhoneNumber;

    private String title;

    private String content;

    public NotifyNewMessageEvent(String requesterPhoneNumber, String title, String content) {
        this.requesterPhoneNumber = requesterPhoneNumber;
        this.title = title;
        this.content = content;
    }
}
