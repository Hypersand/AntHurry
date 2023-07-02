package com.ant.hurry.boundedContext.board.service;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import com.ant.hurry.boundedContext.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BoardServiceTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private BoardService boardService;
    @Autowired
    private Rq rq;

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
        assertThat(canModify.getResultCode()).isEqualTo("S_B-3");
        assertThat(canModify.getMsg()).isEqualTo("수정 가능합니다.");
    }

    @Test
    @DisplayName("canModify 메소드 실패 테스트")
    @WithUserDetails("user1")
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
        assertThat(modify.getResultCode()).isEqualTo("S_B-4");
        assertThat(modify.getMsg()).isEqualTo("게시글이 수정되었습니다.");
    }

    @Test
    @DisplayName("modify 메서드 실패 테스트")
    @WithUserDetails("user1")
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


}
