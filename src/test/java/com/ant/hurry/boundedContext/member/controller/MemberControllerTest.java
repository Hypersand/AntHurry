package com.ant.hurry.boundedContext.member.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.base.security.SecurityConfig;
import com.ant.hurry.boundedContext.member.dto.ProfileRequestDto;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.member.service.PhoneAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;


import java.io.IOException;
import java.util.Optional;



@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {


    @InjectMocks
    MemberController memberController;

    @Mock
    MemberService memberService;

    @Mock
    PhoneAuthService phoneAuthService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    Rq rq;


    MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("프로필 페이지 이동 - 로그인 X")
    void showProfileNotLogin() throws Exception {

        //given
        when(rq.isLogin()).thenReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(get("/usr/member/profile"));


        //then
        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/usr/member/login"))
                .andExpect(redirectedUrl("/usr/member/login"));
    }

    @Test
    @DisplayName("프로필 페이지 이동 - 로그인 O")
    void showProfileLogin() throws Exception {

        //given
        when(rq.isLogin()).thenReturn(true);

        Member member = new Member();
        Optional<ProfileImage> profileImage = Optional.of(new ProfileImage());
        when(rq.getMember()).thenReturn(member);
        when(memberService.findProfileImage(member)).thenReturn(profileImage);

        //when
        ResultActions resultActions = mockMvc.perform(get("/usr/member/profile"));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("usr/member/profile"))
                .andExpect(model().attribute("member", member))
                .andExpect(model().attribute("profileImage", profileImage.orElse(null)));
    }

    @Test
    @DisplayName("프로필 수정 페이지 이동")
    void showProfileEdit() throws Exception {
        Member member = Member.builder()
                .username("user1")
                .nickname("user1")
                .password("1234")
                .phoneAuth(1)
                .phoneNumber("01011112222")
                .build();
        //given
        Optional<ProfileImage> profileImage = Optional.of(new ProfileImage());
        when(rq.isLogin()).thenReturn(true);
        when(rq.getMember()).thenReturn(member);
        when(memberService.findProfileImage(member)).thenReturn(profileImage);

        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setNickname(member.getNickname());
        if(profileImage.isPresent())
            profileRequestDto.setImagePath(profileImage.get().getFullPath());
        else
            profileRequestDto.setImagePath(null);

        //when
        ResultActions resultActions = mockMvc.perform(get("/usr/member/profile_edit"));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfileEdit"))
                .andExpect(view().name("usr/member/profile_edit"))
                .andExpect(model().attribute("profileRequestDto", profileRequestDto));
    }

    @Test
    @DisplayName("프로필 수정 기능 확인 - 정상 입력")
    void updateProfile() throws Exception {
        // Given
        Member member = Member.builder()
                .username("user1")
                .nickname("user1")
                .password("1234")
                .phoneAuth(1)
                .phoneNumber("01011112222")
                .build();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "originalName", "text/plain;UTF-8", "test data".getBytes());
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setNickname(member.getNickname());

        when(rq.getMember()).thenReturn(member);

        // When
        MockHttpServletRequestBuilder requestBuilder = multipart("/usr/member/profile_edit")
                .file(mockMultipartFile)
                .flashAttr("profileRequestDto", profileRequestDto);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        /* -> 요청에 대한 반환값 설정
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

         */

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("updateProfile"))
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(jsonPath("$.message", is("성공")));

        verify(memberService).updateProfile(eq(member), eq(member.getNickname()), any(MultipartFile.class));
    }

    @Test
    @DisplayName("프로필 수정 기능 확인 - 닉네임 실패")
    void updateProfileNickNameFail() throws Exception {
        // Given
        Member member = Member.builder()
                .username("user1")
                .nickname("user1")
                .password("1234")
                .phoneAuth(1)
                .phoneNumber("01011112222")
                .build();

        //MultipartFile
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "originalName", "text/plain;UTF-8", "test data".getBytes());

        //ProfileRequestDto
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setNickname("aaa");


        // When
        MockHttpServletRequestBuilder requestBuilder = multipart("/usr/member/profile_edit")
                .file("file", mockMultipartFile.getBytes())
                .flashAttr("profileRequestDto", profileRequestDto);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        /* json 요청에 대한 결과 확인
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
         */

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("updateProfile"));
    }


    @Test
    @DisplayName("핸드폰 번호 전송 - 성공")
    void phoneAuthSendSuccess() throws Exception {
        // Given
        String phoneNumber = "01012345678";
        String authCode = "123456";

        when(memberService.existsPhoneNumber(phoneNumber)).thenReturn(false);
        when(phoneAuthService.sendSms(phoneNumber)).thenReturn(authCode);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth/send")
                .param("phoneNumber", phoneNumber));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("phoneAuth"))
                .andExpect(jsonPath("$.message", is("인증번호 전송 성공")));
    }

    @Test
    @DisplayName("핸드폰 번호 전송 - 전화번호 중복")
    void phoneAuthSendDuplicate() throws Exception {
        // Given
        String phoneNumber = "01012345678";

        when(memberService.existsPhoneNumber(phoneNumber)).thenReturn(true);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth/send")
                .param("phoneNumber", phoneNumber));

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("phoneAuth"))
                .andExpect(jsonPath("$.message", is("전화번호 중복")));
    }

    // 인증번호 확인 테스트1
    @Test
    @DisplayName("인증번호 확인 테스트 - 성공")
    void checkPhoneAuthCodeTestSuccess() throws Exception {
        // Given
        String phoneNumber = "01012345678";
        String authCode = "123456";

        when(phoneAuthService.getAuthCode(phoneNumber)).thenReturn(authCode);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth/check")
                .param("phoneNumber", phoneNumber)
                .param("authCode", authCode));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("checkAuthCode"))
                .andExpect(jsonPath("$.message", is("인증 성공")));
    }

    // 인증번호 확인 테스트2
    @Test
    @DisplayName("인증번호 확인 테스트 - 만료")
    void checkAuthCodeTestExpired() throws Exception {
        // Given
        String phoneNumber = "01012345678";
        String authCode = "123456";

        when(phoneAuthService.getAuthCode(phoneNumber)).thenReturn(null);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth/check")
                .param("phoneNumber", phoneNumber)
                .param("authCode", authCode));

        // Then
        resultActions
                .andExpect(status().isGone())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("checkAuthCode"))
                .andExpect(jsonPath("$.message", is("인증번호가 만료되었습니다.")));
    }

    // 인증번호 확인 테스트3
    @Test
    @DisplayName("인증번호 확인 테스트 - 실패")
    void checkAuthCodeTestFail() throws Exception {
        // Given
        String phoneNumber = "01012345678";
        String authCode = "123456";

        when(phoneAuthService.getAuthCode(phoneNumber)).thenReturn(authCode);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth/check")
                .param("phoneNumber", phoneNumber)
                .param("authCode", "123123"));

        // Then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("checkAuthCode"))
                .andExpect(jsonPath("$.message", is("인증번호가 일치하지 않습니다.")));
    }

    @Test
    @DisplayName("인증완료 테스트 - 성공")
    void checkPhoneAuthSuccess() throws Exception {
        // Given
        String phoneNumber = "01012345678";

        Member member = new Member();
        RsData<String> rsData = RsData.of("S-2", "전화번호 인증이 완료되었습니다.");
        when(rq.getMember()).thenReturn(member);
        when(memberService.phoneAuthComplete(member, phoneNumber)).thenReturn(rsData);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth")
                .param("phoneNumber", phoneNumber));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("phoneAuthComplete"))
                .andExpect(jsonPath("$.message", is("전화번호 인증이 완료되었습니다.")));
    }
    @Test
    @DisplayName("인증완료 테스트 - 실패1")
    void checkPhoneAuthFail01() throws Exception {
        // Given
        String phoneNumber = "01012345678";

        Member member = new Member();
        RsData<String> rsData = RsData.of("F-2", "전화번호를 입력해서 인증번호를 받아주세요.");
        when(rq.getMember()).thenReturn(member);
        when(memberService.phoneAuthComplete(member, phoneNumber)).thenReturn(rsData);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth")
                .param("phoneNumber", phoneNumber));

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("phoneAuthComplete"))
                .andExpect(jsonPath("$.message", is("전화번호를 입력해서 인증번호를 받아주세요.")));
    }

    @Test
    @DisplayName("인증완료 테스트 - 실패2")
    void checkPhoneAuthFail02() throws Exception {
        // Given
        String phoneNumber = "01012345678";

        Member member = new Member();
        RsData<String> rsData = RsData.of("F-2", "인증번호 검증이 완료되지 않았습니다.");
        when(rq.getMember()).thenReturn(member);
        when(memberService.phoneAuthComplete(member, phoneNumber)).thenReturn(rsData);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth")
                .param("phoneNumber", phoneNumber));

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("phoneAuthComplete"))
                .andExpect(jsonPath("$.message", is("인증번호 검증이 완료되지 않았습니다.")));
    }

    @Test
    @DisplayName("인증완료 테스트 - 실패3")
    void checkPhoneAuthFail03() throws Exception {
        // Given
        String phoneNumber = "01012345678";

        Member member = new Member();
        RsData<String> rsData = RsData.of("F-2", "입력하신 전화번호와 인증번호를 받은 번호가 일치하지 않습니다.");
        when(rq.getMember()).thenReturn(member);
        when(memberService.phoneAuthComplete(member, phoneNumber)).thenReturn(rsData);

        // When
        ResultActions resultActions = mockMvc.perform(post("/usr/member/phoneAuth")
                .param("phoneNumber", phoneNumber));

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("phoneAuthComplete"))
                .andExpect(jsonPath("$.message", is("입력하신 전화번호와 인증번호를 받은 번호가 일치하지 않습니다.")));
    }

}
