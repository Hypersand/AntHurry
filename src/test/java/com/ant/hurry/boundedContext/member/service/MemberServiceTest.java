package com.ant.hurry.boundedContext.member.service;


import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;


    @Test
    @DisplayName("프로필 멤버의 id가 유효하지 않으면 실패 처리")
    @WithUserDetails("user1")
    void profile_member_not_exists() {

        //given
        Long memberId = 999L;

        //when
        RsData<Member> rsData = memberService.validateAndReturnMember(memberId);

        //then
        assertAll(
                () -> assertThat(rsData.getResultCode()).isEqualTo("F_M-2"),
                () -> assertThat(rsData.getMsg()).isEqualTo("접근할 수 있는 권한이 없습니다.")
        );
    }
}