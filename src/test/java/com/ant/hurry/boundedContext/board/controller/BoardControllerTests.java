package com.ant.hurry.boundedContext.board.controller;

import com.ant.hurry.boundedContext.board.entity.BoardType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class BoardControllerTests {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("게시판 작성 입력 폼")
    void shouldRenderCreateBoardPage() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/board/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("createBoard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("board/create"));
    }

    @Test
    @DisplayName("게시판 작성 완료")
    @WithMockUser("test")
    void shouldCreateBoard() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/board/create")
                        .with(csrf())
                        .param("title", "게시판 제목입니다.")
                        .param("content", "게시판 내용입니다.")
                        .param("boardType", String.valueOf(BoardType.나잘해요))
                        .param("address", "서울 관악구 신림동 1662-3")
                        .param("rewardCoin", "100"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("createBoard"))
                .andExpect(status().is2xxSuccessful());
    }

}
