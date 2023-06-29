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

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;


    public ProfileImage(String uploadFileName, String storedFileName, String fullPath) {
        this.uploadFileName = uploadFileName;
        this.storedFileName = storedFileName;
        this.fullPath = fullPath;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void updateProfile(ProfileImage changeProfile) {
        this.uploadFileName = changeProfile.getUploadFileName();
        this.storedFileName = changeProfile.getStoredFileName();
        this.fullPath = changeProfile.getFullPath();
    }
}
