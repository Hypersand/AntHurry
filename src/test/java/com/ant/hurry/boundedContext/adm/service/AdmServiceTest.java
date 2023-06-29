package com.ant.hurry.boundedContext.adm.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.adm.controller.AdmController;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AdmServiceTest {

    @Autowired
    private AdmService admService;

    @Test
    @DisplayName("존재하는 환전신청만 수락할 수 있다.")
    @WithUserDetails("admin")
    void shouldAcceptApply() throws Exception {

        //given
        Long exchangeId = 1L;

        //when
        RsData<Object> rsData = admService.accept(exchangeId);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("S_A-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("성공적으로 처리되었습니다.")
        );
    }

    @Test
    @DisplayName("존재하지 않는 환전신청을 수락하면 실패한다.")
    @WithUserDetails("admin")
    void shouldFailAcceptApplyDueToNotExistsApply() throws Exception {

        //given
        Long exchangeId = 11L;

        //when
        RsData<Object> rsData = admService.accept(exchangeId);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_A-1"),
                () -> assertThat(rsData.getMsg()).isEqualTo("존재하지 않는 환전요청입니다.")
        );
    }
}
