package com.ant.hurry.boundedContext.board.service;

import com.ant.hurry.base.api.dto.AddressDTO;
import com.ant.hurry.base.api.dto.KakaoApiResponseDTO;
import com.ant.hurry.base.api.service.KakaoAddressSearchService;
import com.ant.hurry.base.region.repository.RegionRepository;
import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.BoardDto;
import com.ant.hurry.boundedContext.board.dto.CreateConvertDTO;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import com.ant.hurry.boundedContext.board.repository.BoardRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.ant.hurry.boundedContext.board.code.BoardErrorCode.*;
import static com.ant.hurry.boundedContext.board.code.BoardSuccessCode.*;
import static com.ant.hurry.boundedContext.coin.code.ExchangeErrorCode.COIN_NOT_ENOUGH;
import static com.ant.hurry.boundedContext.coin.code.ExchangeSuccessCode.COIN_ENOUGH;
import static com.ant.hurry.boundedContext.member.code.MemberErrorCode.MEMBER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final Rq rq;
    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final RegionRepository regionRepository;
    private final TradeStatusService tradeStatusService;

    public RsData hasEnoughCoin(int rewardCoin) {
        Member member = memberService.findById(rq.getMember().getId()).orElse(null);
        if (ObjectUtils.isEmpty(member)) {
            return RsData.of(MEMBER_NOT_EXISTS);
        }
        if (member.getCoin() < rewardCoin) {
            return RsData.of(COIN_NOT_ENOUGH);
        }
        member.decreaseCoin(rewardCoin);
        return RsData.of(COIN_ENOUGH);
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
        return RsData.of(CREATE_BOARD);
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
            return RsData.of(NOT_EXISTS_BOARD);
        }
        if (!member.equals(board.getMember())) {
            return RsData.of(CAN_NOT_REMOVE_BOARD);
        }
        return RsData.of(CAN_REMOVE_BOARD);
    }

    @Transactional
    public RsData<Board> delete(Long id) {
        Board board = findById(id).orElse(null);
        tradeStatusService.deleteTradeStatusDueToBoard(id);
        rq.getMember().increaseCoin(board.getRewardCoin());
        boardRepository.delete(board);
        return RsData.of(REMOVE_BOARD);
    }

    public Optional<Board> findById(Long id) {
        return boardRepository.findById(id);
    }

    public Optional<Board> findByIdWithMember(Long id) {
        return boardRepository.findByIdWithMember(id);
    }

    public RsData<Board> canModify(Member member, Long id) {
        Board board = findById(id).orElse(null);
        if (board == null) {
            return RsData.of(NOT_EXISTS_BOARD);
        }

        if (!member.equals(board.getMember())) {
            return RsData.of(CAN_NOT_EDIT_BOARD);
        }
        return RsData.of(CAN_EDIT_BOARD, board);
    }

    @Transactional
    public RsData<Board> modify(Long id, CreateRequest createRequest, Member member) {

        RsData<Board> boardRsData = canModify(member, id);
        if (boardRsData.isFail()) {
            return boardRsData;
        }
        Board board = boardRsData.getData();

        int coin = board.getRewardCoin(); //바뀌기 전

        if (createRequest.getAddress().isBlank()) {
            board.updateBoard(createRequest);
        } else {
            CreateConvertDTO createConvertDTO = addressConvert(createRequest);
            board.updateBoard(createConvertDTO);
        }

        member.increaseCoin(coin);
        member.decreaseCoin(board.getRewardCoin()); //새 금액으로 차감

        return RsData.of(EDIT_BOARD);
    }



    public Slice<BoardDto> getAllBoards(Long lastId, String code, Pageable pageable) {
        return boardRepository.paginationNoOffsetBuilder(lastId, code, pageable);
    }


    public Slice<BoardDto> getOnlineBoards(Long id, String title, TradeType tradeType, Pageable pageable) {
        return boardRepository.onlineBoardPaginationNoOffsetBuilder(id, title, tradeType, pageable);
    }

    public Slice<BoardDto> getMyBoards(Long lastId, Member member, Pageable pageable) {
        return boardRepository.myBoardPaginationNoOffsetBuilder(lastId, member, pageable);
    }

    public Slice<BoardDto> getRegionOfflineBoards(Long lastId, String code, String search, Pageable pageable){
        return boardRepository.regionOfflineBoardPaginationNoOffsetBuilder(lastId, code, search, pageable);
    }

    @Transactional
    public void whenAfterUpdateStatus(TradeStatus tradeStatus) {
        Board board = findById(tradeStatus.getBoard().getId()).orElseThrow();
        if (board.getBoardType() == BoardType.나급해요) {
            tradeStatus.getHelper().increaseCoin(board.getRewardCoin());
        } else if (board.getBoardType() == BoardType.나잘해요) {
            tradeStatus.getHelper().increaseCoin(board.getRewardCoin());
            tradeStatus.getRequester().decreaseCoin(board.getRewardCoin());
        }
    }
}
