package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.boundedContext.tradeStatus.service.TradeStatusService;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.entity.LatestMessage;
import com.ant.hurry.chat.service.ChatMessageService;
import com.ant.hurry.chat.service.ChatRoomService;
import com.ant.hurry.chat.service.LatestMessageService;
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
    private final ChatMessageService chatMessageService;
    private final TradeStatusService tradeStatusService;
    private final LatestMessageService latestMessageService;
    private final Rq rq;

    @GetMapping("/room/{id}")
    public String showRoom(@PathVariable String id, Model model) {
        RsData<ChatRoom> rs = chatRoomService.findByIdAndVerify(id, rq.getMember());

        if (rs.getData() == null) {
            return rq.historyBack(rs.getMsg());
        }

        ChatRoom room = rs.getData();
        Member otherMember = room.getMembers().stream().filter(m -> !m.equals(rq.getMember())).findFirst().get();
        List<ChatMessage> chatMessages = chatMessageService.findByChatRoom(room).getData();

        model.addAttribute("otherMember", otherMember);
        model.addAttribute("chatMessages", chatMessages);
        model.addAttribute("room", room);
        return "chat/room";
    }

    @PostMapping("/room/{id}")
    public String create(@PathVariable("id") Long tradeStatusId) {
        TradeStatus tradeStatus = tradeStatusService.findById(tradeStatusId);
        RsData<ChatRoom> rs = chatRoomService.create(tradeStatus);
        ChatRoom chatRoom = rs.getData();
        return rq.redirectWithMsg("chat/room/%s".formatted(chatRoom.getId()), rs.getMsg());
    }

    @GetMapping("/myRooms")
    public String showMyRooms(Model model) {
        RsData<List<LatestMessage>> rs = latestMessageService.findByMember(rq.getMember());
        List<LatestMessage> latestMessages = rs.getData().stream()
                .filter(lm -> lm != null && (lm.getMessage() != null || lm.getFileMessage() != null)).toList();
        model.addAttribute("latestMessages", latestMessages);
        return "chat/myRooms";
    }

    @GetMapping("/exit/{id}")
    public String exit(@PathVariable String id) {
        ChatRoom chatRoom = chatRoomService.findByIdAndVerify(id, rq.getMember()).getData();
        Member member = rq.getMember();
        RsData rs = chatRoomService.exit(chatRoom, member);
        return rq.redirectWithMsg("chat/myRooms", rs.getMsg());
    }

}
