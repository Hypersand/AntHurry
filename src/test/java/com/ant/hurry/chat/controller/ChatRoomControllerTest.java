package com.ant.hurry.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase

public class ChatRoomControllerTest {

    @Autowired
    ChatRoomController chatRoomController;

}
