package com.ant.hurry.boundedContext.board.repository;

import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select b from Board b join fetch b.member m where b.id = :id")
    Optional<Board> findByIdWithMember(@Param("id") Long id);

    List<Board> findByRegCode(String code);

    List<Board> findByRegCodeAndBoardType(String code, BoardType boardType);
}
