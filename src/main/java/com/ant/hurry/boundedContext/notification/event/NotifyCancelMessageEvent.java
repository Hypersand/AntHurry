package com.ant.hurry.boundedContext.notification.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotifyCancelMessageEvent {

    private String requesterPhoneNumber;

    private String helperPhoneNumber;

    private String contentToRequester;
    private String contentToHelper;

    public NotifyCancelMessageEvent(String requesterPhoneNumber, String helperPhoneNumber, String contentToRequester, String contentToHelper) {
        this.requesterPhoneNumber = requesterPhoneNumber;
        this.helperPhoneNumber = helperPhoneNumber;
        this.contentToRequester = contentToRequester;
        this.contentToHelper = contentToHelper;
    }
}
