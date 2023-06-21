package com.ant.hurry.boundedContext.sms.web;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class SendRequest {
    private String receiverPhoneNumber;
    private String title;
    private String content;

}
