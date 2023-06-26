package com.ant.hurry.base.security;

import com.ant.hurry.boundedContext.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class PhoneAuthenticationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 로그인한 사용자의 정보 가져온다.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //memberService 가져오기
        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        MemberService memberService = webApplicationContext.getBean(MemberService.class);


        String requestUri = request.getRequestURI();

        // 로그인한 사용자의 휴대폰 인증 여부를 확인합니다.
        // 휴대폰 인증 페이지로의 요청은 통과시킵니다.
        if (!requestUri.startsWith("/usr/member/phoneAuth") && auth != null && auth.isAuthenticated() && !memberService.isPhoneAuthenticated(auth.getName())) {
            // 인증하지 않았다면, 핸드폰 인증 페이지로 리다이렉트
            response.sendRedirect("/usr/member/phoneAuth");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
