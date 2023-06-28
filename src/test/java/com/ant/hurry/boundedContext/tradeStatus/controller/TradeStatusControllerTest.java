package com.ant.hurry.boundedContext.tradeStatus.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TradeStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("거래 상테 페이지 접근")
    @WithUserDetails("user3")
    void tradeStatus_access() throws Exception {


        //when
        ResultActions resultActions = mockMvc.perform(get("/trade/list"))
                .andDo(print());


        //then
        resultActions
                .andExpect(handler().handlerType(TradeStatusController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(model().attributeExists("tradeStatusList"))
                .andExpect(view().name("tradeStatus/list"))
                .andExpect(status().isOk());

    }

}