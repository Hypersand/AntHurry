package com.ant.hurry.boundedContext.notification.code;

import com.ant.hurry.base.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationSuccessCode implements Code {
    REDIRECT_NOTIFICATION_LIST_PAGE("S_N-1", "알림목록페이지로 이동합니다."),
    REMOVE_NOTIFICATION("S_N-2", "성공적으로 알림이 삭제되었습니다.");

    private String code;
    private String message;
}
