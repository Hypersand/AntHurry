package com.ant.hurry.base.security;

import com.ant.hurry.boundedContext.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhoneAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private ServletContext servletContext;

    @Mock
    private WebApplicationContext webApplicationContext;

    @Mock
    private MemberService memberService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PhoneAuthenticationFilter filter;

    @BeforeEach
    public void setUp() {
        // Set up the mocks
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(anyString())).thenReturn(webApplicationContext);
        when(webApplicationContext.getBean(MemberService.class)).thenReturn(memberService);
    }

    @Test
    @DisplayName("회원 - 핸드폰 인증 X")
    public void testRedirectToPhoneAuthPage() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/usr/member/profile");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("username");
        when(memberService.isPhoneAuthenticated(anyString())).thenReturn(false);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).sendRedirect("/usr/member/phoneAuth");
    }

    @Test
    @DisplayName("회원 - 핸드폰 인증 O")
    public void testNoRedirect() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/usr/member/profile");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("username");
        when(memberService.isPhoneAuthenticated(anyString())).thenReturn(true);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, never()).sendRedirect(anyString());
    }
}
