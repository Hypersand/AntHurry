package com.ant.hurry.boundedContext.notification.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotifyEndMessageEvent {

    private String requesterPhoneNumber;

    private String helperPhoneNumber;

    private String content;

    public NotifyEndMessageEvent(String requesterPhoneNumber, String helperPhoneNumber, String content) {
        this.requesterPhoneNumber = requesterPhoneNumber;
        this.helperPhoneNumber = helperPhoneNumber;
        this.content = content;
    }
}
