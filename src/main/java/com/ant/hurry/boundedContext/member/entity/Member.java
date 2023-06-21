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

    private String phoneNumber;

    private String providerTypeCode; //일반, 카카오 등 어떤 회원가입인지 구별

    private int coin = 0;


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

    public void increaseCoin(int coinAmount){
        coin += coinAmount;
    }
}
