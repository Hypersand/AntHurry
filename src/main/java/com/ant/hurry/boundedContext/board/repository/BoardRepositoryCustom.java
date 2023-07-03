package com.ant.hurry.boundedContext.board.repository;

import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;

import java.util.List;

public interface BoardRepositoryCustom {
    List<Board> paginationNoOffsetBuilder(Long id, String code, BoardType boardType);
}
