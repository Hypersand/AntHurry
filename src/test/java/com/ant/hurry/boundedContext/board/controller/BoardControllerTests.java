package com.ant.hurry.boundedContext.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(content().string(containsString("""
                        <input type="text" name="title"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="boardType"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="boardType"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="number" name="rewardCoin"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="text" name="address"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="submit"
                        """.stripIndent().trim())));
    }

}
