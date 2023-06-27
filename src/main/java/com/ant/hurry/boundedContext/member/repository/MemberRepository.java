package com.ant.hurry.boundedContext.member.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

}
