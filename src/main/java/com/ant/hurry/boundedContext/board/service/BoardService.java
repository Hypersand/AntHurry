package com.ant.hurry.boundedContext.board.service;

import com.ant.hurry.base.api.dto.AddressDTO;
import com.ant.hurry.base.api.dto.KakaoApiResponseDTO;
import com.ant.hurry.base.api.service.KakaoAddressSearchService;
import com.ant.hurry.base.region.repository.RegionRepository;
import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.CreateConvertDTO;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.repository.BoardRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final Rq rq;
    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final RegionRepository regionRepository;

    public RsData hasEnoughCoin(int rewardCoin) {
        Member member = memberService.findById(rq.getMember().getId()).orElse(null);
        if (ObjectUtils.isEmpty(member)) {
            return RsData.of("F-1", "유저정보가 없습니다.");
        }
        if(member.getCoin() < rewardCoin){
            return RsData.of("F-1", "유저의 코인이 부족합니다.");
        }
        member.decreaseCoin(rewardCoin);
        return RsData.of("S-1", "충분한 코인을 가지고있습니다.");
    }

    @Transactional
    public RsData<Board> write(Member member, CreateConvertDTO createConvertDTO) {
        Board board = Board
                .builder()
                .boardType(createConvertDTO.getBoardType())
                .title(createConvertDTO.getTitle())
                .content(createConvertDTO.getContent())
                .x(createConvertDTO.getX())
                .y(createConvertDTO.getY())
                .rewardCoin(createConvertDTO.getRewardCoin())
                .regCode(createConvertDTO.getRegCode())
                .tradeType(createConvertDTO.getTradeType())
                .member(member)
                .build();
        boardRepository.save(board);
        return RsData.of("S-1", "게시글이 작성되었습니다.");
    }

    public CreateConvertDTO addressConvert(CreateRequest createRequest) {
        Mono<KakaoApiResponseDTO> kakaoApiResult = kakaoAddressSearchService.requestAddressSearch(createRequest.getAddress());
        AddressDTO addressInfo = kakaoApiResult.block().getDocumentDTOList().get(0).getAddress();
        String regCode = getRegCode(addressInfo);

        return CreateConvertDTO
                .builder()
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .boardType(createRequest.getBoardType())
                .rewardCoin(createRequest.getRewardCoin())
                .tradeType(createRequest.getTradeType())
                .x(addressInfo.getX())
                .y(addressInfo.getY())
                .regCode(regCode)
                .build();
    }

    private String getRegCode(AddressDTO addressInfo) {
        String depth3 = addressInfo.getDepth3().split(" ")[0];
        return regionRepository.findByDepth2AndDepth3(addressInfo.getDepth2(), depth3).get().getCode();
    }

    public RsData<Board> canDelete(Member member, Long id) {
        Board board = findById(id).orElse(null);
        if (board == null) {
            return RsData.of("F-1", "없는 게시물입니다..");
        }
        if (!member.equals(board.getMember())) {
            return RsData.of("F-1", "삭제할 권한이 없습니다.");
        }
        return RsData.of("S-1", "삭제 가능합니다.");
    }

    @Transactional
    public RsData<Board> delete(Long id) {
        Board board = findById(id).orElse(null);
        rq.getMember().increaseCoin(board.getRewardCoin());
        boardRepository.delete(board);
        return RsData.of("S-1", "게시글이 삭제되었습니다.");
    }

    public Optional<Board> findById(Long id) {
        return boardRepository.findById(id);
    }

    public List<Board> findByCode(String code) {
        return boardRepository.findByRegCode(code);
    }

    public List<Board> findByCodeAndBoard(String code, BoardType boardType) {
        return boardRepository.findByRegCodeAndBoardType(code, boardType);
    }

    public RsData canModify(Member member, Board board) {
        if (!member.equals(board.getMember())) {
            return RsData.of("F_B-3", "수정할 권한이 없습니다.");
        }
        return RsData.of("S_B-3", "수정 가능합니다.");
    }

    @Transactional
    public RsData<Board> modify(Board board, CreateRequest createRequest) {

        if (createRequest.getAddress().isBlank()) {
            board.updateBoard(createRequest);
        } else {
            CreateConvertDTO createConvertDTO = addressConvert(createRequest);
            board.updateBoard(createConvertDTO);
        }

        return RsData.of("S_B-4", "게시글이 수정되었습니다.");
    }
}
