package com.ant.hurry.boundedContext.board.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.repository.BoardRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final Rq rq;
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    public RsData hasEnoughCoin(int rewardCoin) {
        Member member = memberService.findById(rq.getMember().getId()).orElse(null);
        if(ObjectUtils.isEmpty(member)){
            return RsData.of("F-1", "유저정보가 없습니다.");
        }
        if(member.getCoin() < rewardCoin){
            return RsData.of("F-1", "유저의 코인이 부족합니다.");
        }
        member.decreaseCoin(rewardCoin);
        return RsData.of("S-1", "충분한 코인을 가지고있습니다.");
    }

    @Transactional
    public RsData<Board> write(Member member, CreateRequest createRequest){
        Board board = Board
                .builder()
                .boardType(createRequest.getBoardType())
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .x(createRequest.getX())
                .y(createRequest.getY())
                .rewardCoin(createRequest.getRewardCoin())
                .regCode(createRequest.getRegCode())
                .member(member)
                .build();
        boardRepository.save(board);
        return RsData.of("S-1", "게시글이 작성되었습니다.");
    }

    public void addressConvert(CreateRequest createRequest) {
//        createRequest.addressConvert();
    }
}
