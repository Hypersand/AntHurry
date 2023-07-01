package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
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
    private final LatestMessageService latestMessageService;
    private final Rq rq;

    @GetMapping("/room/{id}")
    public String showRoom(@PathVariable String id, Model model) {
        RsData<ChatRoom> rs = chatRoomService.findByIdAndVerify(id, rq.getMember());

        if (rs.isFail()) {
            return rq.historyBack(rs.getMsg());
        }

        ChatRoom chatRoom = rs.getData();
        Member otherMember = chatRoom.getMembers()
                .stream().filter(m -> !m.equals(rq.getMember())).findFirst().orElse(null);

        if (otherMember == null) return rq.historyBack("존재하지 않는 회원입니다.");

        model.addAttribute("otherMember", otherMember);
        model.addAttribute("chatMessages", chatMessageService.findByChatRoom(chatRoom).getData());
        model.addAttribute("room", chatRoom);
        return "chat/room";
    }

    @PostMapping("/room")
    public String create(TradeStatus tradeStatus) {
        RsData<ChatRoom> rs = chatRoomService.create(tradeStatus);
        ChatRoom chatRoom = rs.getData();
        return rq.redirectWithMsg("chat/room/%s".formatted(chatRoom.getId()), rs.getMsg());
    }

    @GetMapping("/myRooms")
    public String showMyRooms(Model model) {
        RsData<List<LatestMessage>> rs = latestMessageService.findByMember(rq.getMember());
        List<LatestMessage> latestMessages = rs.getData().stream()
                .filter(lm -> lm != null && lm.getMessage() != null).toList();
        model.addAttribute("latestMessages", latestMessages);
        return "chat/myRooms";
    }

    @GetMapping("/exit/{id}")
    public String exit(@PathVariable String id) {
        RsData<ChatRoom> rs = chatRoomService.findByIdAndVerify(id, rq.getMember());
        RsData exitRs = chatRoomService.exit(rs.getData(), rq.getMember());
        return rq.redirectWithMsg("chat/myRooms", exitRs.getMsg());
    }

}
