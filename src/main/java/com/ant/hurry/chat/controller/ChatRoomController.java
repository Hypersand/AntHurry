package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.entity.LatestMessage;
import com.ant.hurry.chat.service.ChatMessageService;
import com.ant.hurry.chat.service.ChatRoomService;
import com.ant.hurry.chat.service.LatestMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
@PreAuthorize("isAuthenticated()")
public class ChatRoomController {

    private final ChatMessageController chatMessageController;
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

        List<ChatMessage> chatMessages = chatMessageService.findByChatRoomId(chatRoom.getId()).getData();
        chatMessages.forEach(cm -> {
            cm.markAsRead();
            chatMessageService.markAsRead(cm);
        });

        Member otherMember = chatRoom.getMembers()
                .stream().filter(m -> !m.getUsername().equals(rq.getMember().getUsername()))
                .findFirst().orElse(null);

        if (otherMember == null) return rq.historyBack("존재하지 않는 회원입니다.");

        model.addAttribute("otherMember", otherMember);
        model.addAttribute("chatMessages", chatMessageService.findAllMessagesByChatRoomId(id).getData());
        model.addAttribute("room", chatRoom);
        return "chat/room";
    }

    @GetMapping("/myRooms")
    public String showMyRooms(Model model) {
        List<ChatRoom> chatRooms = chatRoomService.findByMember(rq.getMember()).getData().stream()
                .filter(cr -> !cr.getExitedMembers().contains(rq.getMember())).toList();
        Map<ChatRoom, LatestMessage> map = new HashMap<>();
        chatRooms.forEach(cr -> map.put(cr, latestMessageService.findByChatRoomId(cr.getId()).getData()));

        model.addAttribute("chatRooms", map);
        return "chat/myRooms";
    }

    @GetMapping("/exit/{id}")
    public String exit(@PathVariable String id) {
        RsData<ChatRoom> rs = chatRoomService.findByIdAndVerify(id, rq.getMember());
        ChatRoom chatRoom = rs.getData();
        Optional<Member> otherMember = chatRoom.getMembers().stream().filter(m -> !m.equals(rq.getMember())).findFirst();

        if (otherMember.isEmpty()) {
            return rq.historyBack("존재하지 않는 회원입니다."); // 수정 필요
        }

        chatMessageController.sendExitMessage(id, otherMember.get().getUsername());

        chatRoomService.exit(rs.getData(), rq.getMember());
        return "redirect:/chat/myRooms";
    }

}
