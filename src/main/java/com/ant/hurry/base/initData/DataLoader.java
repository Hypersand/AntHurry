package com.ant.hurry.base.initData;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    @Value("${custom.admin.username}")
    private String username;

    @Value("${custom.admin.password}")
    private String password;

    @Value("${custom.admin.phoneNumber}")
    private String phoneNumber;


    @Autowired
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {

        Member admin = Member.builder()
                .username(username)
                .nickname("admin")
                .password(password)
                .phoneAuth(1)
                .phoneNumber(phoneNumber)
                .tmpPhoneNumber(phoneNumber)
                .build();

        memberRepository.save(admin);
    }
}
