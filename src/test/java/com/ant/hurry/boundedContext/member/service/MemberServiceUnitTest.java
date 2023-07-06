package com.ant.hurry.boundedContext.member.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.base.s3.S3ProfileUploader;
import com.ant.hurry.boundedContext.coin.service.CoinService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.entity.Role;
import com.ant.hurry.boundedContext.member.entity.RoleType;
import com.ant.hurry.boundedContext.member.repository.MemberRepository;
import com.ant.hurry.boundedContext.member.repository.ProfileImageRepository;
import com.ant.hurry.boundedContext.member.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceUnitTest {

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

    @Mock
    RoleRepository roleRepository;


    @Test
    @DisplayName("whenSocialLogin() - 기존 사용자 로그인")
    void whenSocialLoginExist() {
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
    void whenSocialLoginNew() {
        //given
        String username = "KAKAO_123123123123";
        String providerTypeCode = "KAKAO";

        Member member = new Member();
        Optional<Role> role = Optional.of(new Role(RoleType.ROLE_MEMBER));
        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(roleRepository.findByName(RoleType.ROLE_MEMBER)).thenReturn(role);
        //when
        RsData<Member> result = memberService.whenSocialLogin(providerTypeCode, username);

        //then
        assertThat(result.getResultCode()).isEqualTo("S-1");
        assertThat(result.getMsg()).isEqualTo("회원가입이 완료되었습니다.");
        assertThat(result.getData()).isEqualTo(member);
    }

    @Test
    @DisplayName("findByUsername()")
    void findByUsername() {

        //given
        String username = "KAKAO_123123123123";
        Optional<Member> opMember = Optional.of(
                Member.builder()
                        .username(username)
                        .nickname(username.substring(0, 12))
                        .providerTypeCode("KAKAO")
                        .password("1234")
                        .phoneAuth(1)
                        .phoneNumber("01011112222")
                        .build()
        );
        when(memberRepository.findByUsername(username)).thenReturn(opMember);


        //when
        Optional<Member> result = memberService.findByUsername(username);
        Member member = result.get();

        //then
        assertThat(member).isEqualTo(opMember.get());
        assertThat(member.getUsername()).isEqualTo(opMember.get().getUsername());
        assertThat(member.getNickname()).isEqualTo(opMember.get().getNickname());
        assertThat(member.getUsername()).isEqualTo(opMember.get().getUsername());
    }

    @Test
    @DisplayName("isPhoneAuthenticated() - 성공")
    void isPhoneAuthenticatedSuccess() {

        //given
        String username = "KAKAO_123123123123";
        Optional<Member> opMember = Optional.of(
                Member.builder()
                        .username(username)
                        .nickname(username.substring(0, 12))
                        .providerTypeCode("KAKAO")
                        .password("1234")
                        .phoneAuth(1)
                        .phoneNumber("01011112222")
                        .build()
        );
        when(memberRepository.findByUsername(username)).thenReturn(opMember);


        //when
        boolean phoneAuthenticated = memberService.isPhoneAuthenticated(username);


        //then
        assertThat(phoneAuthenticated).isTrue();

    }

    @Test
    @DisplayName("isPhoneAuthenticated() - 실패")
    void isPhoneAuthenticatedFail() {

        //given
        String username = "KAKAO_123123123123";
        Optional<Member> opMember = Optional.of(
                Member.builder()
                        .username(username)
                        .nickname(username.substring(0, 12))
                        .providerTypeCode("KAKAO")
                        .password("1234")
                        .phoneAuth(0)
                        .phoneNumber(null)
                        .build()
        );
        when(memberRepository.findByUsername(username)).thenReturn(opMember);


        //when
        boolean phoneAuthenticated = memberService.isPhoneAuthenticated(username);


        //then
        assertThat(phoneAuthenticated).isFalse();

    }

    @Test
    @DisplayName("updatePhoneAuth()")
    void updatePhoneAuthTest() {

        // Given
        Member member = new Member();
        int beforePhoneAuth = member.getPhoneAuth();

        // When
        memberService.updatePhoneAuth(member);

        // Then
        assertThat(beforePhoneAuth).isNotEqualTo(member.getPhoneAuth());
        assertThat(member.getPhoneAuth()).isEqualTo(1);
    }

    @Test
    @DisplayName("getMember()")
    void getMember() {

        // Given
        Member member = new Member();
        when(rq.getMember()).thenReturn(member);

        // When
        Member getMember = memberService.getMember();

        // Then
        assertThat(getMember).isEqualTo(member);
    }


    @Test
    @DisplayName("updateProfile() - Exchange Image")
    void updateProfileExchange() throws IOException {

        //given
        Member member = new Member();
        ProfileImage profileImage = new ProfileImage("test1.png", "test1.png", "test1.png");
        profileImage.setMember(member);
        String currentImageName = profileImage.getUploadFileName();

        String fileName = "test2.png";
        String fileContent = "content";
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "img/*", fileContent.getBytes());

        ProfileImage updateProfileImage = new ProfileImage(file.getOriginalFilename(), file.getOriginalFilename(), file.getOriginalFilename());
        updateProfileImage.setMember(member);
        when(profileImageRepository.findByMember(member)).thenReturn(Optional.of(profileImage));
        when(profileUploader.updateFile(profileImage.getStoredFileName(), file)).thenReturn(updateProfileImage);


        //when
        String nickname = "newNickname";
        memberService.updateProfile(member, nickname, file);

        // Then
        ProfileImage result = profileImageRepository.findByMember(member).get();
        assertThat(currentImageName).isNotEqualTo(result.getUploadFileName());
        assertThat(result.getUploadFileName()).isEqualTo(fileName);
        assertThat(member.getNickname()).isEqualTo("newNickname");
    }

    @Test
    @DisplayName("updateProfile() - New Image")
    void updateProfileNew() throws IOException {

        // Given
        Member member = new Member(); // Replace this with actual Member instantiation if necessary
        ProfileImage profileImage = new ProfileImage();
        profileImage.setMember(member);

        String fileName = "test.txt";
        String fileContent = "content";
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "text/plain", fileContent.getBytes());


        when(profileImageRepository.findByMember(member)).thenReturn(Optional.empty());
        when(profileUploader.uploadFile(file)).thenReturn(profileImage);
        when(profileImageRepository.save(any(ProfileImage.class))).thenReturn(profileImage);

        // When
        String nickname = "newNickname";
        memberService.updateProfile(member, nickname, file);


        // Then;
        assertThat(member.getNickname()).isEqualTo("newNickname");
        verify(profileImageRepository).save(profileImage);
    }
}
