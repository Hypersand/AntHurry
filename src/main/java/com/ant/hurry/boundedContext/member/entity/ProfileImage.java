package com.ant.hurry.boundedContext.member.entity;

import com.ant.hurry.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProfileImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String uploadFileName;

    private String storedFileName;

    private String fullPath;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

}
