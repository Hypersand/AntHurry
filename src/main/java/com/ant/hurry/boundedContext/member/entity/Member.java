package com.ant.hurry.boundedContext.member.entity;

import com.ant.hurry.base.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Setter
    @Builder.Default
    private long coin = 0;


    private int phoneAuth;


    public boolean isPhoneAuth(){
        if(phoneAuth == 1 && phoneNumber != null)
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

    @Builder.Default
    private double rating = 0;

    @Builder.Default
    private int reviewCount = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "member_roles",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if(this.roles != null){
            for (Role role : this.roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName().toString()));
            }
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

    public boolean isAdmin() {
        return "admin".equals(nickname);
    }
}
