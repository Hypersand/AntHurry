package com.ant.hurry.boundedContext.member.controller;

import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.review.controller.ReviewController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MemberControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;


    @Test
    @DisplayName("유효하지 않는 멤버 id의 프로필 조회")
    @WithUserDetails("user1")
    void profile_member_not_exists() throws Exception {

        //given
        Long memberId = 999L;

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/usr/member/profile/{memberId}", memberId)).andDo(print());

        //then
        resultActions.andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfile"))
                .andExpect(model().attributeDoesNotExist("member"))
                .andExpect(view().name("common/js"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("user1의 프로필 조회")
    @WithUserDetails("user1")
    void profile_member_exists() throws Exception {

        //given
        Long memberId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/usr/member/profile/{memberId}", memberId)).andDo(print());

        //then
        resultActions.andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfile"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("completeTradeCount"))
                .andExpect(model().attributeExists("reviewCount"))
                .andExpect(status().isOk())
                .andExpect(view().name("usr/member/usrCheck"));
    }

}