package com.ant.hurry.chat.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.entity.LatestMessage;
import com.ant.hurry.chat.service.ChatMessageService;
import com.ant.hurry.chat.service.ChatRoomService;
import com.ant.hurry.chat.service.LatestMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
@PreAuthorize("isAuthenticated()")
@Tag(name = "ChatRoomController", description = "채팅방, 채팅 목록에 대한 컨트롤러")
public class ChatRoomController {

    private final ChatMessageController chatMessageController;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final LatestMessageService latestMessageService;
    private final MemberService memberService;
    private final Rq rq;

    @Operation(summary = "채팅방 조회", description = "개별적인 채팅방을 조회합니다.")
    @GetMapping("/room/{id}")
    public String showRoom(@PathVariable String id, Model model) {
        RsData<ChatRoom> rs = chatRoomService.findByIdAndVerify(id, rq.getMember());

        if (rs.isFail()) {
            return rq.historyBack(rs.getMsg());
        }

        ChatRoom chatRoom = rs.getData();

        Member otherMember = chatRoom.getMembers()
                .stream().filter(m -> !m.getUsername().equals(rq.getMember().getUsername()))
                .findFirst().orElse(null);

        if (otherMember == null) return rq.historyBack("존재하지 않는 회원입니다.");

        Optional<ProfileImage> myProfileImage = memberService.findProfileImage(rq.getMember());
        Optional<ProfileImage> otherProfileImage = memberService.findProfileImage(otherMember);

        LatestMessage latestMessage = latestMessageService.findByChatRoomId(id).getData();

        if (latestMessage.getWriter() != null && !latestMessage.getWriter().equals(rq.getMember().getUsername())) {
            latestMessageService.save(latestMessage.markAsRead());
        }

        model.addAttribute("otherMember", otherMember);
        model.addAttribute("chatMessages", chatMessageService.findAllMessagesByChatRoomId(id).getData());
        model.addAttribute("myProfileImage", myProfileImage.orElse(null));
        model.addAttribute("otherProfileImage", otherProfileImage.orElse(null));
        model.addAttribute("room", chatRoom);
        return "chat/room";
    }

    @Operation(summary = "채팅 목록 조회", description = "유저가 속한 채팅 목록을 조회합니다.")
    @GetMapping("/myRooms")
    public String showMyRooms(Model model) {
        List<ChatRoom> chatRooms = chatRoomService.findByMember(rq.getMember()).getData().stream()
                .sorted(Comparator.comparing(ChatRoom::getCreatedAt)).toList();
        Map<ChatRoom, LatestMessage> map = new LinkedHashMap<>();

        for (ChatRoom chatRoom : chatRooms) {
            LatestMessage latestmessage =
                    latestMessageService.findByChatRoomId(chatRoom.getId()).getData().getMessage() == null ?
                            null : latestMessageService.findByChatRoomId(chatRoom.getId()).getData();
            if (latestmessage != null) {
                map.put(chatRoom, latestmessage);
            }
        }

        model.addAttribute("chatRooms", map);
        return "chat/myRooms";
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방 나가기 버튼을 통해 채팅방에서 퇴장합니다.")
    @GetMapping("/exit/{id}")
    public String exit(@PathVariable String id) {
        RsData<ChatRoom> rs = chatRoomService.findByIdAndVerify(id, rq.getMember());

        RsData exitRs = chatRoomService.exit(rs.getData(), rq.getMember());

        if (exitRs.isFail()) {
            rq.historyBack(exitRs.getMsg());
        }

        chatMessageController.sendExitMessage(id);

        return "redirect:/chat/myRooms";
    }

}
