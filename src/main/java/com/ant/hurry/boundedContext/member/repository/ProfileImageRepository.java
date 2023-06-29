package com.ant.hurry.boundedContext.member.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByMember(Member member);
}
