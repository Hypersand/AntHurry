package com.ant.hurry.chat.controller;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("채팅방을 생성합니다.")
    @WithUserDetails("user1")
    void chatRoom_create() throws Exception {
        Member otherMember = Member.builder().build();
        Member user1 = memberService.findByUsername("user1").get();
        TradeStatus tradeStatus = TradeStatus.builder()
                .id(1L).status(Status.BEFORE).requester(otherMember).helper(user1).build();

        ResultActions resultActions1 = mvc
                .perform(post("/chat/room")
                        .with(csrf()))
                .andDo(print());

        resultActions1
                .andExpect(handler().handlerType(ChatRoomController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection());
    }

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