package com.ant.hurry.boundedContext.adm.controller;

import com.ant.hurry.boundedContext.board.controller.BoardController;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AdmControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("관리자만 환전 신청 목록에 들어갈 수 있다.")
    @WithUserDetails("admin")
    void shouldRenderApplyListPage() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/adm/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(AdmController.class))
                .andExpect(handler().methodName("showApplyList"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("adm/home/applyList"));
    }

    @Test
    @DisplayName("관리자가 아니면 환전 신청 목록에 들어갈 수 없다.")
    @WithUserDetails("user1")
    void shouldFailApplyListPageDueToNotAdmin() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/adm/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(AdmController.class))
                .andExpect(handler().methodName("showApplyList"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("관리자는 환전신청을 수락할 수 있다.")
    @WithUserDetails("admin")
    void shouldAcceptApply() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/adm/complete/1"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(AdmController.class))
                .andExpect(handler().methodName("acceptApplication"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("관리자가 아니면 환전신청을 수락할 수 없다.")
    @WithUserDetails("user1")
    void shouldFailAcceptApplyDueToNotAdmin() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/adm/complete/1"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(AdmController.class))
                .andExpect(handler().methodName("acceptApplication"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("존재하지 않는 환전신청을 수락하면 실패한다.")
    @WithUserDetails("admin")
    void shouldFailAcceptApplyDueToNotExistsApply() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/adm/complete/3"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(AdmController.class))
                .andExpect(handler().methodName("acceptApplication"))
                .andExpect(status().is4xxClientError());
    }
}
