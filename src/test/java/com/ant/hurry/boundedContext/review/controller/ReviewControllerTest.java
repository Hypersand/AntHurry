package com.ant.hurry.boundedContext.review.controller;

import com.ant.hurry.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("리뷰 작성페이지 접근 시 해당 거래에 대한 리뷰를 이미 작성한 상황")
    @WithMockUser(username = "user1")
    void review_already_exists() throws Exception {

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/review/create/1")
        ).andDo(print());


        //then
        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("showCreate"))
                .andExpect(model().attributeDoesNotExist("opponentNickname"))
                .andExpect(view().name("common/js"))
                .andExpect(model().attribute("resultCode", "F_R-1"))
                .andExpect(status().is4xxClientError());
    }



    @Test
    @DisplayName("리뷰 작성페이지 접근 시 권한이 없는 멤버인 상황")
    @WithMockUser(username = "user3")
    void review_member_accessDenied() throws Exception {

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/review/create/1")
        ).andDo(print());


        //then
        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("showCreate"))
                .andExpect(model().attributeDoesNotExist("opponentNickname"))
                .andExpect(view().name("common/js"))
                .andExpect(model().attribute("resultCode", "F_M-2"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @DisplayName("리뷰 작성페이지 접근 시 내가 Requester인 상황")
    @WithMockUser(username = "user3")
    void review_member_requester() throws Exception {

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/review/create/4")
        ).andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("showCreate"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("opponentNickname", "User4"))
                .andExpect(view().name("review/create"));

    }


    @Test
    @DisplayName("리뷰 작성페이지 접근 시 내가 Helper인 상황")
    @WithMockUser(username = "user2")
    void review_member_helper() throws Exception {

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/review/create/1")
        ).andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("showCreate"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("opponentNickname", "User1"))
                .andExpect(view().name("review/create"));

    }


}