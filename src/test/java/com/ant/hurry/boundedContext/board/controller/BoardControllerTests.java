package com.ant.hurry.boundedContext.board.controller;

import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BoardControllerTests {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("게시판 작성 입력 폼")
    @WithMockUser("test")
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

//    @Test
//    @DisplayName("게시판 작성 완료")
//    @WithMockUser(username = "test", roles = "admin")
//    void shouldCreateBoard() throws Exception {
//        // WHEN
//        ResultActions resultActions = mvc
//                .perform(post("/board/create")
//                        .param("title", "게시판 제목입니다.")
//                        .param("content", "게시판 내용입니다.")
//                        .param("boardType", String.valueOf(BoardType.나잘해요))
//                        .param("tradeType", String.valueOf(TradeType.온라인))
//                        .param("address", "서울 성북구 낙산길 243-15")
//                        .param("rewardCoin", "100"))
//                .andDo(print());
//
//        // THEN
//        resultActions
//                .andExpect(handler().handlerType(BoardController.class))
//                .andExpect(handler().methodName("createBoard"))
//                .andExpect(status().is3xxRedirection());
//    }

}
