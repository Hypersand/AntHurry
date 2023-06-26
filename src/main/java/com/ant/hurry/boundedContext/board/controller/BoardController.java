package com.ant.hurry.boundedContext.board.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.board.dto.CreateConvertDTO;
import com.ant.hurry.boundedContext.board.dto.CreateRequest;
import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.board.entity.BoardType;
import com.ant.hurry.boundedContext.board.entity.TradeType;
import com.ant.hurry.base.region.entity.Region;
import com.ant.hurry.base.region.service.RegionSearchService;
import com.ant.hurry.boundedContext.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final Rq rq;
    private final RegionSearchService regionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showCreateBoard(Model model) {
        model.addAttribute("boardTypes", BoardType.values());
        model.addAttribute("tradeTypes", TradeType.values());
        return "board/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createBoard(@Valid CreateRequest createRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("boardTypes", BoardType.values());
        model.addAttribute("tradeTypes", TradeType.values());
        if (bindingResult.hasErrors()) {
            return "board/create";
        }
        RsData checkUserCoin = boardService.hasEnoughCoin(createRequest.getRewardCoin());
        if(checkUserCoin.isFail()){
            return rq.historyBack(checkUserCoin);
        }
        CreateConvertDTO boardInfo = boardService.addressConvert(createRequest);
        RsData<Board> boardRs = boardService.write(rq.getMember(), boardInfo);
        return rq.redirectWithMsg("/board/enterRegion?code=" + boardInfo.getRegCode(), boardRs);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("{id}")
    public String showBoard(Model model,  @PathVariable("id") Long id) {
        Board board = boardService.getBoard(id);
        model.addAttribute("board", board);
        return "/board/board";
    }

    /**
     *지역 검색하면 나오는 지역 리스트를 보여준다.
     **/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/selectRegion")
    public String showRegion(Model model){
        List<Region> regions = regionService.findAll();
        model.addAttribute("regions", regions);
        return "board/selectRegion";
    }


    /**
     * selectRegion에서 지역을 선택하면 해당 지역의 게시판으로 이동한다.
     **/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/enterRegion")
    public String enterRegion(@RequestParam("code")String code, Model model) {
        Region region = regionService.findByCode(code).orElseThrow();
        model.addAttribute("region", region);
        return "board/enterRegion";
    }


}

