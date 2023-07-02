package com.ant.hurry.boundedContext.board.controller;

import com.ant.hurry.base.region.service.RegionSearchService;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import com.ant.hurry.standard.util.Ut;
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

import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RegionSearchService regionSearchService;


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
                .andExpect(status().is2xxSuccessful());
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
                .perform(get("/board/5"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("showBoard"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("게시판 삭제 완료")
    @WithMockUser("user1")
    void shouldDeleteBoard() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/board/5")
                                .with(csrf())
                )
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
                .perform(
                        delete("/board/5")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("deleteBoard"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("해당 지역의 게시판으로 들어가서 게시글 확인")
    @WithUserDetails("user1")
    void checkEnterRegionBoard() throws Exception {
        regionSearchService.selectPattern();
        //삼산동 게시판으로 들어가서 글 확인
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/board/enterRegion")
                        .param("code", "2823710500"))
                .andDo(print());


        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("enterRegion"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("board/enterRegion"))
                .andExpect(content().string(containsString("휴지좀")))
                .andExpect(content().string(containsString("1000")));
    }

    @Test
    @DisplayName("게시글 수정 GET")
    @WithMockUser("user1")
    void modifyBoard() throws Exception {
        regionSearchService.selectPattern();

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/board/modify/5"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("showModify"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("board/modify"))
                .andExpect(content().string(containsString("휴지좀")))
                .andExpect(content().string(containsString("1000")));
    }

    @Test
    @DisplayName("게시글 수정 POST")
    @WithMockUser("user1")
    void updateBoard() throws Exception {
        regionSearchService.selectPattern();

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/board/modify/5")
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
                .andExpect(handler().methodName("modify"))
                .andExpect(status().is3xxRedirection());

    }

}
