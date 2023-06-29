package com.ant.hurry.boundedContext.member.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.coin.entity.CoinChargeLog;
import com.ant.hurry.boundedContext.coin.service.CoinChargeService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.ant.hurry.boundedContext.coin.code.ExchangeErrorCode.COIN_NOT_ENOUGH;
import static com.ant.hurry.boundedContext.coin.code.ExchangeSuccessCode.COIN_ENOUGH;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CoinChargeService coinChargeService;
    private final Rq rq;



    // 소셜 로그인(카카오, 구글, 네이버) 로그인이 될 때 마다 실행되는 함수
    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username) {
        Optional<Member> opMember = memberRepository.findByUsername(username); // username 예시 : KAKAO__1312319038130912, NAVER__1230812300

        if (opMember.isPresent())
            return RsData.of("S-2", "로그인 되었습니다.", opMember.get());

        // 소셜 로그인를 통한 가입시 비번은 없다.
        return createAndSave(username, "", "", providerTypeCode); // 최초 로그인 시 딱 한번 실행
    }

    private RsData<Member> createAndSave(String username, String password, String phone, String providerTypeCode) {
        Member member = Member
                .builder()
                .username(username)
                .nickname(username) //소셜 로그인 초기 닉네임은 username 과 동일
                .password(passwordEncoder.encode(password))
                .phoneNumber(phone)
                .providerTypeCode(providerTypeCode)
                .coin(0)
                .phoneAuth(0)
                .build();

        Member savedMember = memberRepository.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", savedMember);
    }

    @Transactional
    public long addCoin(Member member, long price, String eventType) {
        CoinChargeLog coinChargeLog = coinChargeService.addCoin(member, price, eventType);

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

    public RsData<?> phoneAuthComplete(Member member, String phoneNumber) {
        if(member.getTmpPhoneNumber() == null){
            return RsData.of("F-2", "전화번호를 입력해서 인증번호를 받아주세요.");
        }
        if (member.getPhoneAuth() != 1) {
            return RsData.of("F-2", "인증번호 검증이 완료되지 않았습니다.");
        }
        if (!member.getTmpPhoneNumber().equals(phoneNumber)) {
            return RsData.of("F-2", "입력하신 전화번호와 인증번호를 받은 번호가 일치하지 않습니다.");
        }

        return RsData.of("S-2", "전화번호 인증이 완료되었습니다.");
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

    public Member getMember() {
        return rq.getMember();
    }

    public RsData checkCanCharge( Long id, String orderId) {
        Member member = rq.getMember();
        Long orderIdInput = Long.parseLong(orderId.split("__")[1]);

        if(id != orderIdInput || member.getId() != id){
            return RsData.of("F_M-3", "로그인한 회원과 충전할 회원이 일치하지 않습니다.");
        }
        return RsData.of("S_M-3", "충전 가능합니다.");
    }

    public RsData canExchange(long money) {
        Member member = rq.getMember();
        if(member.getCoin() < money){
            return RsData.of(COIN_NOT_ENOUGH);
        }
        return RsData.of(COIN_ENOUGH);
    }
}
