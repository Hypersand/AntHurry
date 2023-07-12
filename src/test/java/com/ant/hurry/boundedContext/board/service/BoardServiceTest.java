package com.ant.hurry.boundedContext.board.service;

import com.ant.hurry.base.region.service.RegionSearchService;
import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.BoardDto;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import com.ant.hurry.boundedContext.board.repository.BoardRepository;
import com.ant.hurry.boundedContext.member.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;
    @Autowired
    private Rq rq;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private RegionSearchService regionSearchService;


    @AfterEach
    void cleanUp() {
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("canModify 메소드 성공 테스트")
    @WithUserDetails("user1")
    void canModifySuccess() throws Exception {
        // Given
        Long boardId = 5L;
        Member member = rq.getMember();

        // When
        RsData<Board> canModify = boardService.canModify(member, boardId);


        // Then
        assertThat(canModify.getResultCode()).isEqualTo("S_B-4");
        assertThat(canModify.getMsg()).isEqualTo("수정 가능합니다.");
    }

    @Test
    @DisplayName("canModify 메소드 실패 테스트")
    @WithUserDetails("user4")
    void canModifyFail() throws Exception {
        // Given
        Long boardId = 1L; // user1이 작성한 게시글이 아닌 게시글
        Member member = rq.getMember();

        // When
        RsData<Board> canModify = boardService.canModify(member, boardId);

        // Then
        assertThat(canModify.getResultCode()).isEqualTo("F_B-3");
        assertThat(canModify.getMsg()).isEqualTo("수정할 권한이 없습니다.");
    }

    @Test
    @DisplayName("modify 메서드 성공 테스트")
    @WithUserDetails("user1")
    void modifySuccess() throws Exception {
        // Given
        Long boardId = 5L;
        Member member = rq.getMember();
        CreateRequest createRequest = new CreateRequest("수정된 제목", "수정된 내용", BoardType.나잘해요, TradeType.온라인, "", 1000);

        RsData<Board> modify = boardService.modify(boardId, createRequest, member);


        // Then
        assertThat(modify.getResultCode()).isEqualTo("S_B-5");
        assertThat(modify.getMsg()).isEqualTo("게시글이 수정되었습니다.");
    }

    @Test
    @DisplayName("modify 메서드 실패 테스트")
    @WithUserDetails("user3")
    void modifyFail() throws Exception {
        // Given
        Long boardId = 1L; // user1이 작성한 게시글이 아닌 게시글
        Member member = rq.getMember();
        CreateRequest createRequest = new CreateRequest("수정된 제목", "수정된 내용", BoardType.나잘해요, TradeType.온라인, "", 1000);

        RsData<Board> modify = boardService.modify(boardId, createRequest, member);

        // Then
        assertThat(modify.getResultCode()).isEqualTo("F_B-3");
        assertThat(modify.getMsg()).isEqualTo("수정할 권한이 없습니다.");
    }

    @Test
    @DisplayName("지역 게시판 1번째 페이지 불러오기")
    @WithUserDetails("user1")
    void noOffSet() throws Exception {


        //when
        Slice<BoardDto> boards = boardRepository.paginationNoOffsetBuilder(null, "2823710500", PageRequest.ofSize(10));

        //then
        assertThat(boards.getContent().size()).isEqualTo(10);
        assertThat(boards.getContent().get(0).getTitle()).isEqualTo("제목50");
        assertThat(boards.getContent().get(9).getTitle()).isEqualTo("제목41");
    }

    @Test
    @DisplayName("지역 게시판 2번째 페이지 불러오기")
    @WithUserDetails("user1")
    void noOffSet2() throws Exception {

        //when
        Slice<BoardDto> boards = boardRepository.paginationNoOffsetBuilder(41L, "2823710500", PageRequest.ofSize(10));

        //then
        assertThat(boards.getContent().size()).isEqualTo(10);
        assertThat(boards.getContent().get(0).getTitle()).isEqualTo("제목40");
        assertThat(boards.getContent().get(9).getTitle()).isEqualTo("제목31");
    }

    @Test
    @DisplayName("지역 게시판 마지막 페이지에서는 isLast가 true, 마지막이 아니면 false")
    @WithUserDetails("user1")
    void checkLast() throws Exception {

        //when
        Slice<BoardDto> boards = boardRepository.paginationNoOffsetBuilder(21L, "2823710500", PageRequest.ofSize(10));

        //then
        assertThat(boards.isLast()).isFalse();

        //when
        Slice<BoardDto> boards2 = boardRepository.paginationNoOffsetBuilder(11L, "2823710500", PageRequest.ofSize(10));

        //then
        assertThat(boards2.isLast()).isTrue();
    }

    @Test
    @DisplayName("온라인 게시판 검색하여 불러오기")
    @WithUserDetails("user1")
    void onlineBoard() throws Exception {

        //when
        Slice<BoardDto> boards = boardRepository.onlineBoardPaginationNoOffsetBuilder(null, "제목", TradeType.온라인, PageRequest.ofSize(10));

        //then
        assertThat(boards.getContent().size()).isEqualTo(10);
        assertThat(boards.getContent().get(0).getTitle()).isEqualTo("제목50");
        assertThat(boards.getContent().get(9).getTitle()).isEqualTo("제목41");
    }

    @Test
    @DisplayName("온라인 게시판 2번째 페이지 불러오기")
    @WithUserDetails("user1")
    void onlineBoard2() throws Exception {

        //when
        Slice<BoardDto> boards = boardRepository.onlineBoardPaginationNoOffsetBuilder(41L, "제목", TradeType.온라인, PageRequest.ofSize(10));

        //then
        assertThat(boards.getContent().size()).isEqualTo(10);
        assertThat(boards.getContent().get(0).getTitle()).isEqualTo("제목40");
        assertThat(boards.getContent().get(9).getTitle()).isEqualTo("제목31");
    }

    @Test
    @DisplayName("온라인 게시판 마지막 페이지에서는 isLast가 true, 마지막이 아니면 false")
    @WithUserDetails("user1")
    void onlineBoardLast() throws Exception {

        //when
        Slice<BoardDto> boards = boardRepository.onlineBoardPaginationNoOffsetBuilder(null, "제목", TradeType.온라인, PageRequest.ofSize(10));

        //then
        assertThat(boards.isLast()).isFalse();

        //when
        Slice<BoardDto> boards2 = boardRepository.onlineBoardPaginationNoOffsetBuilder(41L, "제목", TradeType.온라인, PageRequest.ofSize(10));

        //then
        assertThat(boards2.isLast()).isTrue();
    }

    @Test
    @DisplayName("온라인 게시판에서 검색했는데 게시물 없을 경우")
    @WithUserDetails("user1")
    void onlineBoardEmpty() throws Exception {

        //when
        Slice<BoardDto> boards = boardRepository.onlineBoardPaginationNoOffsetBuilder(null, "없는 제목", TradeType.온라인, PageRequest.ofSize(10));

        //then
        assertThat(boards.getContent().size()).isEqualTo(0);
    }



}
