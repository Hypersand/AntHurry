package com.ant.hurry.boundedContext.member.controller;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ant.hurry.base.rq.Rq;
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

}
