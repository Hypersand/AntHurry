package com.ant.hurry.boundedContext.board.repository;

import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface BoardRepositoryCustom {
    Slice<Board> paginationNoOffsetBuilder(Long id, String code, Pageable pageable);

    Slice<Board> onlineBoardPaginationNoOffsetBuilder(Long id, String content, TradeType tradeType, Pageable pageable);
}
