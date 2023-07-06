package com.ant.hurry.chat.baseEntity;

public interface Message {
    String getMessage();
    boolean isNotRead();
    void markAsRead();
}
