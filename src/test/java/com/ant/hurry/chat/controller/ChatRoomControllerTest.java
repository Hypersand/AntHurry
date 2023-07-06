package com.ant.hurry.chat.controller;

import com.ant.hurry.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
@AutoConfigureTestDatabase
public class ChatRoomControllerTest {

    @Autowired
    ChatRoomController chatRoomController;
    @Autowired
    MemberService memberService;
    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("채팅 목록을 조회합니다.")
    @WithUserDetails("user1")
    void chatRoom_showMyRooms() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/chat/myRooms"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChatRoomController.class))
                .andExpect(handler().methodName("showMyRooms"))
                .andExpect(view().name("chat/myRooms"))
                .andExpect(status().is2xxSuccessful());
    }

}