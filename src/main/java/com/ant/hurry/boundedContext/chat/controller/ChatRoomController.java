package com.ant.hurry.boundedContext.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.boundedContext.chat.entity.ChatRoom;
import com.ant.hurry.boundedContext.chat.service.ChatRoomService;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final TradeStatusService tradeStatusService;
    private final Rq rq;

    @GetMapping("/room/{id}")
    public String showRoom(@PathVariable Long id, Model model) {
        ChatRoom room = chatRoomService.findById(id);
        model.addAttribute("room", room);
        return "chat/room";
    }

    @GetMapping
    public String findAll(Model model) {
        List<TradeStatus> tradeStatuses = tradeStatusService.findByMember(rq.getMember());
        List < ChatRoom > chatRooms = chatRoomService.findByTradeStatus(tradeStatuses);
        model.addAttribute("chatRooms", chatRooms);
        return "chat/myRooms";
    }

    @PostMapping
    public String create(TradeStatus tradeStatus) {
        ChatRoom chatRoom = chatRoomService.create(tradeStatus);
        return "redirect:/room/%d".formatted(chatRoom.getId());
    }

}
