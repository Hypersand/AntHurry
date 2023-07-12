package com.ant.hurry.boundedContext.member.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.base.s3.S3ProfileUploader;
import com.ant.hurry.boundedContext.coin.entity.CoinChargeLog;
import com.ant.hurry.boundedContext.coin.service.CoinService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.entity.Role;
import com.ant.hurry.boundedContext.member.entity.RoleType;
import com.ant.hurry.boundedContext.member.repository.MemberRepository;
import com.ant.hurry.boundedContext.member.repository.ProfileImageRepository;
import com.ant.hurry.boundedContext.member.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.ant.hurry.boundedContext.coin.code.ExchangeErrorCode.*;
import static com.ant.hurry.boundedContext.coin.code.ExchangeSuccessCode.COIN_ENOUGH;
import static com.ant.hurry.boundedContext.coin.code.ExchangeSuccessCode.SUCCESS_CHARGE;
import static com.ant.hurry.boundedContext.member.code.MemberErrorCode.*;
import static com.ant.hurry.boundedContext.member.code.MemberSuccessCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CoinService coinService;
    private final Rq rq;

    private final S3ProfileUploader profileUploader;
    private final ProfileImageRepository profileImageRepository;

    private final RoleRepository roleRepository;



    // 소셜 로그인(카카오, 구글, 네이버) 로그인이 될 때 마다 실행되는 함수
    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username) {
        Optional<Member> opMember = memberRepository.findByUsername(username); // username 예시 : KAKAO__1312319038130912, NAVER__1230812300

        if (opMember.isPresent())
            return RsData.of(SUCCESS_LOGIN, opMember.get());

        // 소셜 로그인를 통한 가입시 비번은 없다.
        return createAndSave(username, "", null, providerTypeCode); // 최초 로그인 시 딱 한번 실행
    }

    private RsData<Member> createAndSave(String username, String password, String phone, String providerTypeCode) {
        //기본적으로 회원은 ROLE_MEMBER 라는 권한을 가진다.
        Role memberRole = roleRepository.findByName(RoleType.ROLE_MEMBER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(memberRole);

        Member member = Member
                .builder()
                .username(username)
                .nickname(username.substring(0,12)) //소셜 로그인 초기 닉네임은 username 과 동일
                .password(passwordEncoder.encode(password))
                .phoneNumber(phone)
                .providerTypeCode(providerTypeCode)
                .coin(0)
                .phoneAuth(0)
                .roles(roles)
                .build();
        Member savedMember = memberRepository.save(member);
        return RsData.of(SUCCESS_SIGNUP, savedMember);
    }

    @Transactional
    public long addCoin(Member member, long price, String eventType) {
        CoinChargeLog coinChargeLog = coinService.addCoin(member, price, eventType);

        long newCoin = getCoin(member) + coinChargeLog.getPrice();
        member.setCoin(newCoin);
        memberRepository.save(member);

        return newCoin;
    }

    public long getCoin(Member member) {
        return member.getCoin();
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public boolean isPhoneAuthenticated(String username) {

        Optional<Member> findMember = findByUsername(username);
        Member member = findMember.get();
        if(member.isPhoneAuth())
            return true;
        return false;
    }

    @Transactional
    public void updateTmpPhone(Member member, String tmpPhoneNumber) {
        member.updateTmpPhone(tmpPhoneNumber);
    }

    @Transactional
    public void updatePhoneAuth(Member member) {
        member.updatePhoneAuth();
    }

    public RsData<String> phoneAuthComplete(Member member, String phoneNumber) {
        if(member.getTmpPhoneNumber() == null){
            return RsData.of(NOT_INPUT_PHONE_NUMBER);
        }
        if (member.getPhoneAuth() != 1) {
            return RsData.of(NOT_CERTIFICATION_NUMBER);
        }
        if (!member.getTmpPhoneNumber().equals(phoneNumber)) {
            return RsData.of(NOT_MATCH_NUMBER);
        }

        return RsData.of(CERTIFICATION_PHONE_NUMBER);
    }

    @Transactional
    public void updatePhoneNumber(Member member, String phoneNumber) {
        member.updatePhoneNumber(phoneNumber);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public boolean existsPhoneNumber(String phoneNumber){
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional
    public void updateProfile(Member member,String nickname, MultipartFile file) throws IOException {
        member.updateProfile(nickname);
        if(file != null && !file.isEmpty()){
            //기존에 프로필 이미지가 있으면 기존거 삭제하고 업로드
            Optional<ProfileImage> findProfileImage = profileImageRepository.findByMember(member);
            if(findProfileImage.isPresent()){
                ProfileImage existProfileImage = findProfileImage.get();
                ProfileImage changeProfile = profileUploader.updateFile(existProfileImage.getStoredFileName(), file);
                existProfileImage.updateProfile(changeProfile);
            }
            else{
                ProfileImage profileImage = profileUploader.uploadFile(file);
                profileImage.setMember(member);
                ProfileImage saveProfileImage = profileImageRepository.save(profileImage);
            }
        }
    }

    public Optional<ProfileImage> findProfileImage(Member member) {
        return profileImageRepository.findByMember(member);
    }
    public Member getMember() {
        return rq.getMember();
    }

    public RsData checkCanCharge( Long id, String orderId) {
        Member member = rq.getMember();
        Long orderIdInput = Long.parseLong(orderId.split("__")[1]);

        if(id != orderIdInput || member.getId() != id){
            return RsData.of(NOT_MATCH_MEMBER);
        }
        return RsData.of(SUCCESS_CHARGE);

    }

    public RsData canExchange(long money) {
        if(money == 0){
            return RsData.of(CANNOT_EXCHANGE);
        }
        Member member = rq.getMember();
        if(member.getCoin() < money){
            return RsData.of(COIN_NOT_ENOUGH);
        }
        return RsData.of(COIN_ENOUGH);
    }

    public Optional<Member> findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    public RsData<Member> validateAndReturnMember(Long id) {

        Member currentMember = getMember(); //접속한 유저
        Member profileMember = findById(id).orElse(null); //프로필 유저

        if (currentMember == null || profileMember == null) {
            return RsData.of(CANNOT_ACCESS);
        }

        return RsData.successOf(profileMember);
    }
}
