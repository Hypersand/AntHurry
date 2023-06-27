package com.ant.hurry.boundedContext.board.controller;

import com.ant.hurry.base.region.service.RegionSearchService;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BoardControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RegionSearchService regionSearchService;

    @Autowired
    private

    @Test
    @DisplayName("게시판 작성 입력 폼")
    @WithUserDetails("user1")
    void shouldRenderCreateBoardPage() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/board/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("showCreateBoard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("board/create"));
    }

    @Test
    @DisplayName("게시판 작성 완료")
    @WithMockUser("user3")
    void shouldCreateBoard() throws Exception {
        regionSearchService.selectPattern();
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/board/create")
                        .with(csrf())
                        .param("title", "게시판 제목입니다.")
                        .param("content", "게시판 내용입니다.")
                        .param("boardType", String.valueOf(BoardType.나잘해요))
                        .param("tradeType", String.valueOf(TradeType.온라인))
                        .param("address", "")
                        .param("rewardCoin", "100"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("createBoard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("게시판 작성 실패 - 폼입력 누락")
    @WithMockUser("user3")
    void shouldFailToCreateBoardDueToMissingFormInput() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/board/create")
                        .with(csrf())
                        .param("title", "게시판 제목입니다.")
                        .param("content", "")
                        .param("boardType", "")
                        .param("tradeType", String.valueOf(TradeType.온라인))
                        .param("address", "서울 성동구 마조로 42")
                        .param("rewardCoin", "100"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("createBoard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bindingResult"));

        resultActions = mvc
                .perform(post("/board/create")
                        .with(csrf())
                        .param("title", "")
                        .param("content", "게시판 내용입니다.")
                        .param("boardType", String.valueOf(BoardType.나잘해요))
                        .param("tradeType", String.valueOf(TradeType.온라인))
                        .param("address", "서울 성동구 마조로 42")
                        .param("rewardCoin", "100"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("createBoard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bindingResult"));
    }

    @Test
    @DisplayName("게시판 상세페이지")
    @WithMockUser("user3")
    void shouldRenderBoardPage() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/board/1"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("showBoard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("board/board"));
    }

    @Test
    @DisplayName("게시판 삭제 완료")
    @WithMockUser("user1")
    void shouldDeleteBoard() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/board/1"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("deleteBoard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("게시판 삭제 실패 - 권한 없음")
    @WithMockUser("user3")
    void shouldFailToDeleteBoardDueToInsufficientPermissions() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/board/1"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("deleteBoard"))
                .andExpect(status().is4xxClientError());
    }
}
