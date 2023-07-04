package com.ant.hurry.boundedContext.member.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.base.s3.S3ProfileUploader;
import com.ant.hurry.boundedContext.coin.service.CoinService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.repository.MemberRepository;
import com.ant.hurry.boundedContext.member.repository.ProfileImageRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MemberRepository memberRepository;
    @Mock
    CoinService coinService;
    @Mock
    Rq rq;
    @Mock
    S3ProfileUploader profileUploader;
    @Mock
    ProfileImageRepository profileImageRepository;


    @Test
    @DisplayName("whenSocialLogin() - 기존 사용자 로그인")
    void whenSocialLoginExist(){
        //given
        String username = "KAKAO_123123123123";
        String providerTypeCode = "KAKAO";
        Optional<Member> opMember = Optional.of(
                new Member()
        );
        when(memberRepository.findByUsername(username)).thenReturn(opMember);

        //when
        RsData<Member> result = memberService.whenSocialLogin(providerTypeCode, username);

        //then
        assertThat(result.getResultCode()).isEqualTo("S-2");
        assertThat(result.getMsg()).isEqualTo("로그인 되었습니다.");
        assertThat(result.getData()).isEqualTo(opMember.get());
    }

    @Test
    @DisplayName("whenSocialLogin() - 신규 회원 로그인")
    void whenSocialLoginNew(){
        //given
        String username = "KAKAO_123123123123";
        String providerTypeCode = "KAKAO";

        Member member = new Member();
        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        //when
        RsData<Member> result = memberService.whenSocialLogin(providerTypeCode, username);

        //then
        assertThat(result.getResultCode()).isEqualTo("S-1");
        assertThat(result.getMsg()).isEqualTo("회원가입이 완료되었습니다.");
        assertThat(result.getData()).isEqualTo(member);
    }
}