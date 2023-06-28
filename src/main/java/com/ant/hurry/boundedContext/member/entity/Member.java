package com.ant.hurry.boundedContext.member.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;


    @Column(unique = true)
    private String nickname;

    private String password;

    @Column(unique = true)
    private String phoneNumber;

    private String tmpPhoneNumber;

    private String providerTypeCode; //일반, 카카오 등 어떤 회원가입인지 구별

    private int coin;

    private int phoneAuth;


    public boolean isPhoneAuth(){
        if(phoneAuth == 1 && !phoneNumber.trim().equals("") && !phoneNumber.isEmpty())
            return true;
        return false;
    }

    public void updateTmpPhone(String tmpPhoneNumber){
        this.tmpPhoneNumber = tmpPhoneNumber;
        this.phoneAuth = 0;
    }

    public void updatePhoneAuth() {
        this.phoneAuth = 1;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    private double rating = 0;

    private int reviewCount = 0;


    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("member"));

        if ("admin".equals(nickname)) {
            grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
        }

        return grantedAuthorities;
    }

    public void decreaseCoin(int coinAmount){
        coin -= coinAmount;
    }

    public void increaseCoin(int coinAmount) {
        coin += coinAmount;
    }
    public void updateRating(double rating) {
        this.rating = rating;
        this.reviewCount += 1;
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }
}
