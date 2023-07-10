package com.ant.hurry.boundedContext.board.repository;

import com.ant.hurry.boundedContext.board.dto.BoardDto;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface BoardRepositoryCustom {
    Slice<BoardDto> paginationNoOffsetBuilder(Long id, String code, Pageable pageable);

    Slice<BoardDto> onlineBoardPaginationNoOffsetBuilder(Long id, String content, TradeType tradeType, Pageable pageable);

    Slice<BoardDto> regionOfflineBoardPaginationNoOffsetBuilder(Long id, String code, String search, Pageable pageable);
}
