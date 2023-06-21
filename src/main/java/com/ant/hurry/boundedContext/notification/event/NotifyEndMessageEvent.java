package com.ant.hurry.boundedContext.notification.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotifyEndMessageEvent {

    private String requesterPhoneNumber;

    private String helperPhoneNumber;

    private String title;

    private String content;

    public NotifyEndMessageEvent(String requesterPhoneNumber, String helperPhoneNumber, String title, String content) {
        this.requesterPhoneNumber = requesterPhoneNumber;
        this.helperPhoneNumber = helperPhoneNumber;
        this.title = title;
        this.content = content;
    }
}
