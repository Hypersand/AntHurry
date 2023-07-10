package com.ant.hurry.boundedContext.board.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.BoardDto;
import com.ant.hurry.boundedContext.board.dto.CreateConvertDTO;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import com.ant.hurry.base.region.entity.Region;
import com.ant.hurry.base.region.service.RegionSearchService;
import com.ant.hurry.boundedContext.board.service.BoardService;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
@Tag(name = "BoardController" , description = "게시글 정보 API")
public class BoardController {

    private final BoardService boardService;
    private final Rq rq;
    private final RegionSearchService regionService;
    private final TradeStatusService tradeStatusService;
    private final MemberService memberService;

    @Operation(summary = "게시판 작성 조회", description = "게시판 작성 페이지를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showCreateBoard(Model model) {
        model.addAttribute("boardTypes", BoardType.values());
        model.addAttribute("tradeTypes", TradeType.values());
        return "board/create";
    }

    @Operation(summary = "게시판 작성", description = "게시판 작성을 합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createBoard(@Valid CreateRequest createRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("boardTypes", BoardType.values());
        model.addAttribute("tradeTypes", TradeType.values());
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            return "board/create";
        }
        if (createRequest.getAddress().isBlank()) {
            return rq.historyBack("주소를 입력해주세요.");
        }
        if(createRequest.getBoardType().equals(BoardType.나급해요)){
            RsData checkUserCoin = boardService.hasEnoughCoin(createRequest.getRewardCoin());
            if (checkUserCoin.isFail()) {
                return rq.historyBack(checkUserCoin);
            }
        }
        CreateConvertDTO boardInfo = boardService.addressConvert(createRequest);
        RsData<Board> boardRs = boardService.write(rq.getMember(), boardInfo);
        return rq.redirectWithMsg("/board/enterRegion?code=" + boardInfo.getRegCode(), boardRs);
    }

    @Operation(summary = "게시판 조회", description = "게시판을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showBoard(Model model, @PathVariable("id") Long id) {
        Board board = boardService.findByIdWithMember(id).orElse(null);
        if (board == null) {
            return rq.historyBack("존재하지 않는 게시판 입니다.");
        }
        Optional<ProfileImage> profileImage = memberService.findProfileImage(board.getMember());
        model.addAttribute("profileImage", profileImage.orElse(null));
        model.addAttribute("board", board);
        return "board/board";
    }

    @Operation(summary = "게시판 삭제", description = "유저가 만든 게시판을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String deleteBoard(@PathVariable("id") Long id) {
        RsData<Board> canDeleteBoard = boardService.canDelete(rq.getMember(), id);
        if (canDeleteBoard.isFail()) {
            return rq.historyBack(canDeleteBoard);
        }
        RsData<Board> deleteBoard = boardService.delete(id);
        return rq.redirectWithMsg("/board/selectRegion", deleteBoard);
    }

    @Operation(summary = "게시판 수정 조회", description = "유저가 만든 게시판 수정페이지를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(@PathVariable Long id, Model model) {

        RsData<Board> canModifyBoard = boardService.canModify(rq.getMember(), id);
        if (canModifyBoard.isFail()) {
            return rq.historyBack(canModifyBoard);
        }

        model.addAttribute("board", canModifyBoard.getData());
        model.addAttribute("boardTypes", BoardType.values());
        model.addAttribute("tradeTypes", TradeType.values());

        return "board/modify";
    }

    @Operation(summary = "게시판 수정", description = "유저가 만든 게시판을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, @Valid CreateRequest createRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            return "board/modify";
        }


        RsData<Board> modifyBoard = boardService.modify(id, createRequest, rq.getMember());
        if (modifyBoard.isFail()) {
            return rq.historyBack(modifyBoard);
        }


        return rq.redirectWithMsg("/board/" + id, modifyBoard);
    }

    @Operation(summary = "지역검색", description = "지역 검색시 지역 리스트를 보여줍니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/selectRegion")
    public String showRegion(Model model) {
        List<Region> regions = regionService.findAll();
        model.addAttribute("regions", regions);
        return "board/selectRegion";
    }

    @Operation(summary = "지역 게시판들 조회", description = "선택한 지역의 게시판들을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/enterRegion")
    public String enterRegion(@RequestParam(value = "lastId", required = false) Long lastId,
                              @RequestParam("code") String code,
                              Model model) {


        Region region = regionService.findByCode(code).orElseThrow();

        Slice<BoardDto> boards = boardService.getAllBoards(lastId, code, PageRequest.ofSize(10));

        model.addAttribute("boards", boards.getContent());
        model.addAttribute("region", region);

        return "board/enterRegion";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/enterRegion/{lastId}")
    @ResponseBody
    @Operation(summary = "지역별 게시판의 전체 게시글 조회")
    public ResponseEntity<?> enterRegion(@PathVariable("lastId") Long lastId,
                                         @RequestParam("code") String code) {

        Slice<BoardDto> boards = boardService.getAllBoards(lastId, code, PageRequest.ofSize(10));
        Map<String, Object> map = new HashMap<>();
        map.put("boardList", boards.getContent());
        return ResponseEntity.ok(map);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/{code}")
    @Operation(summary = "게시판 검색", description = "게시판을 검색합니다.")
    public String searchBoard(@PathVariable("code") String code,
                              @Valid SearchForm searchForm,
                              Model model) {

        Region region = regionService.findByCode(code).orElseThrow();

        Slice<BoardDto> boards = boardService.getRegionOfflineBoards(null, code, searchForm.getTitle(), PageRequest.ofSize(10));

        model.addAttribute("boards", boards.getContent());
        model.addAttribute("content", searchForm.getTitle());
        model.addAttribute("region", region);

        return "board/search";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/{code}/{lastId}")
    @ResponseBody
    @Operation(summary = "지역별 게시판에서 검색된 게시글 조회")
    public ResponseEntity<?> searchBoard(@PathVariable("code") String code,
                                         @PathVariable(value = "lastId", required = false) Long lastId,
                                         @RequestParam("title") String title) {

        Slice<BoardDto> boards = boardService.getRegionOfflineBoards(lastId, code, title, PageRequest.ofSize(10));
        Map<String, Object> map = new HashMap<>();
        map.put("boardList", boards.getContent());
        return ResponseEntity.ok(map);
    }

    @AllArgsConstructor
    @Getter
    public static class SearchForm {
        @NotBlank
        @Size(min = 2, max = 30)
        private final String title;


    }

    @Operation(summary = "온라인 게시판 조회", description = "거래 유형이 온라인인 게시판들을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/online")
    public String showOnlineBoard(@Valid SearchForm searchForm, Model model) {

        Slice<BoardDto> boards = boardService.getOnlineBoards(null, searchForm.getTitle(), TradeType.온라인 ,PageRequest.ofSize(10));

        model.addAttribute("boards", boards);
        model.addAttribute("content", searchForm.getTitle());
        return "board/online";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/online/{lastId}")
    @ResponseBody
    @Operation(summary = "온라인 게시판에서 검색된 게시글 조회")
    public ResponseEntity<?> showOnlineBoard(@PathVariable("lastId") Long lastId,
                                              @RequestParam("title") String title) {

        Slice<BoardDto> boards = boardService.getOnlineBoards(lastId, title, TradeType.온라인, PageRequest.ofSize(10));
        Map<String, Object> map = new HashMap<>();
        map.put("boardList", boards.getContent());
        return ResponseEntity.ok(map);
    }

}

