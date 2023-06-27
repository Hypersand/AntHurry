package com.ant.hurry.chat.controller;

import com.ant.hurry.base.code.BasicErrorCode;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.service.ChatMessageService;
import com.ant.hurry.chat.service.ChatRoomService;
import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.chat.entity.ChatRoom;
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

import static com.ant.hurry.base.code.BasicErrorCode.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final TradeStatusService tradeStatusService;
    private final Rq rq;

    @GetMapping("/room/{id}")
    public String showRoom(@PathVariable String id, Model model) {
        ChatRoom room = chatRoomService.findById(id).getData();
        model.addAttribute("room", room);
        return "chat/room";
    }

    @GetMapping("/myRooms")
    public String findAll(Model model) {
        List<TradeStatus> tradeStatuses = tradeStatusService.findByMember(rq.getMember());
        List<ChatRoom> chatRooms = chatRoomService.findByTradeStatus(tradeStatuses).getData();
        model.addAttribute("chatRooms", chatRooms);
        return "chat/myRooms";
    }

    @PostMapping("/create")
    public String create(TradeStatus tradeStatus) {
        RsData<ChatRoom> rs = chatRoomService.create(tradeStatus);
        ChatRoom chatRoom = rs.getData();
        return rq.redirectWithMsg("chat/room/%s".formatted(chatRoom.getId()), rs.getMsg());
    }

    @GetMapping("/delete/{id}")
    public String deleteSoft(@PathVariable String id) {
        ChatRoom chatRoom = chatRoomService.findById(id).getData();
        RsData rs = chatRoomService.deleteSoft(chatRoom);
        return rq.redirectWithMsg("chat/myRooms", rs.getMsg());
    }

    @GetMapping("/delete/hard/{id}")
    public String deleteHard(@PathVariable String id) {
        if(!rq.getMember().getNickname().equals("admin")) {
            return rq.historyBack(UNAUTHORIZED.getMessage());
        }
        ChatRoom chatRoom = chatRoomService.findById(id).getData();
        RsData rs = chatRoomService.delete(chatRoom);
        return rq.redirectWithMsg("chat/myRooms", rs.getMsg());
    }
}
