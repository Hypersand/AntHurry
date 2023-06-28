package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.service.ChatRoomService;
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
    private final Rq rq;

    @GetMapping("/room/{id}")
    public String showRoom(@PathVariable String id, Model model) {
        ChatRoom room = chatRoomService.findById(id).getData();
        model.addAttribute("room", room);
        return "chat/room";
    }

    @GetMapping("/myRooms")
    public String showMyRooms(Model model) {
        List<ChatRoom> chatRooms = chatRoomService.findByMember(rq.getMember()).getData();
        model.addAttribute("chatRooms", chatRooms);
        return "chat/myRooms";
    }

    @PostMapping("/create")
    public String create(TradeStatus tradeStatus) {
        RsData<ChatRoom> rs = chatRoomService.create(tradeStatus);
        ChatRoom chatRoom = rs.getData();
        return rq.redirectWithMsg("chat/room/%s".formatted(chatRoom.getId()), rs.getMsg());
    }

    @GetMapping("/exit/{id}")
    public String exit(@PathVariable String id) {
        ChatRoom chatRoom = chatRoomService.findById(id).getData();
        Member member = rq.getMember();
        RsData rs = chatRoomService.exit(chatRoom, member);
        return rq.redirectWithMsg("chat/myRooms", rs.getMsg());
    }

}
