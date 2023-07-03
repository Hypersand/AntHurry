package com.ant.hurry.boundedContext.board.repository;

import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ant.hurry.boundedContext.board.entity.QBoard.board;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Board> paginationNoOffsetBuilder(Long id, String code, BoardType boardType) {
        // id < 파라미터를 첫 페이지에서는 사용하지 않기 위한 동적 쿼리
        BooleanExpression idLt = id != null ? board.id.lt(id) : null;
        BooleanExpression codeEq = code != null ? board.regCode.eq(code) : null;
        BooleanExpression boardTypeEq = boardType != null ? board.boardType.eq(boardType) : null;

        return jpaQueryFactory.selectFrom(board)
                .where(idLt, codeEq, boardTypeEq)
                .orderBy(board.id.desc())
                .limit(10)
                .fetch();
    }


}
