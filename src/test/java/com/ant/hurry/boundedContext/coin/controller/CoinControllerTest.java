package com.ant.hurry.boundedContext.coin.controller;

import com.ant.hurry.boundedContext.coin.entity.BankType;
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
class CoinControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("환전소 입력 폼")
    @WithUserDetails("user1")
    void shouldRenderExchangePage() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/coin/exchange"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("exchangePoint"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("coin/exchange"));
    }

    @Test
    @DisplayName("환전 완료")
    @WithMockUser("user1")
    void shouldCreateBoard() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/coin/exchange")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "1234567890")
                        .param("money", "1000")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("progressExchange"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("환전 실패 - 코인 부족")
    @WithMockUser("user3")
    void shouldFailExchangeDueToNotEnoughMoney() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/coin/exchange")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "1234567890")
                        .param("money", "100000")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("progressExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("환전 실패 - 0원 환전")
    @WithMockUser("user3")
    void shouldFailExchangeDueToExchangeZeroMoney() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/coin/exchange")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "1234567890")
                        .param("money", "0")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("progressExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("환전 신청 수정")
    @WithMockUser("user1")
    void shouldEditApplyExchange() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(patch("/coin/exchange/1")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "987654321")
                        .param("money", "100")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("editApplyExchange"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("환전 신청 수정 실패 - 권한이 없음.")
    @WithMockUser("user3")
    void shouldFailEditApplyExchangeDueToAuthority() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(patch("/coin/exchange/1")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "987654321")
                        .param("money", "100")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("editApplyExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("환전 신청 수정 실패 - 존재하지 않는 환전 신청")
    @WithMockUser("user1")
    void shouldFailEditApplyExchangeDueToNotExists() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(patch("/coin/exchange/5")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "987654321")
                        .param("money", "100")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("editApplyExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("환전 신청 수정 실패 - 0원 환전")
    @WithMockUser("user1")
    void shouldFailEditApplyExchangeDueToExchangeZeroMoney() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(patch("/coin/exchange/5")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "987654321")
                        .param("money", "0")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("editApplyExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("환전 신청 수정 실패 - 코인 부족")
    @WithMockUser("user1")
    void shouldFailEditApplyExchangeDueToNotEnoughMoney() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(patch("/coin/exchange/5")
                        .with(csrf())
                        .param("bankType", String.valueOf(BankType.국민))
                        .param("accountNumber", "987654321")
                        .param("money", "1000000")
                        .param("holderName", "홍길동"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("editApplyExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("환전 신청 삭제")
    @WithMockUser("user1")
    void shouldDeleteApplyExchange() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/coin/exchange/1")
                        .with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("cancelApplyExchange"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("환전 신청 삭제 실패 - 권한없음")
    @WithMockUser("user3")
    void shouldFailDeleteApplyExchangeDueToAuthority() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/coin/exchange/1")
                        .with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("cancelApplyExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("환전 신청 삭제 실패 - 존재하지 않는 환전신청")
    @WithMockUser("user1")
    void shouldFailDeleteApplyExchangeDueToNotExists() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/coin/exchange/5")
                        .with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("cancelApplyExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("완료된 환전신청 삭제")
    @WithMockUser("user1")
    void shouldDeleteCompleteApplyExchange() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/coin/exchange/2")
                        .with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("cancelApplyExchange"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("완료된 환전신청 삭제 실패 - 존재하지 않는 환전신청")
    @WithMockUser("user1")
    void shouldFailDeleteCompleteApplyExchangeDueToNotExists() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/coin/exchange/5")
                        .with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("cancelApplyExchange"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("완료된 환전신청 삭제 실패 - 권한 없음.")
    @WithMockUser("user3")
    void shouldFailDeleteCompleteApplyExchangeDueToAuthority() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/coin/exchange/2")
                        .with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CoinController.class))
                .andExpect(handler().methodName("cancelApplyExchange"))
                .andExpect(status().is4xxClientError());
    }
}