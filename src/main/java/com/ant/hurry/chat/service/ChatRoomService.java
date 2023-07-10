package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.entity.DeletedChatRoom;
import com.ant.hurry.chat.event.EventAfterDeletedChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import com.ant.hurry.chat.repository.DeletedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ant.hurry.base.code.BasicErrorCode.UNAUTHORIZED;
import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_ALREADY_EXITED;
import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_NO_EXISTS;
import static com.ant.hurry.chat.code.ChatRoomSuccessCode.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final DeletedChatRoomRepository deletedChatRoomRepository;
    private final ApplicationEventPublisher publisher;

    public RsData<ChatRoom> findById(String id) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);

        return chatRoom.map(room -> RsData.of(CHATROOM_FOUND, room))
                .orElseGet(() -> RsData.of(CHATROOM_NO_EXISTS));
    }

    public RsData<ChatRoom> findByIdAndVerify(String id, Member member) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);

        if (chatRoom.isEmpty()) {
            return RsData.of(CHATROOM_NO_EXISTS);
        }

        ChatRoom foundchatRoom = chatRoom.get();

        if (foundchatRoom.getMembers().stream().noneMatch(m -> m.getUsername().equals(member.getUsername()))) {
            return RsData.of(UNAUTHORIZED);
        }

        return RsData.of(CHATROOM_FOUND, foundchatRoom);
    }

    public RsData<List<ChatRoom>> findByMember(Member member) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(member);
        chatRooms.removeIf(cm -> cm.getExitedMembersId().contains(member.getId()));

        return RsData.of(CHATROOM_FOUND, chatRooms);
    }

    public RsData<ChatRoom> findByTradeStatusId(Long id) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByTradeStatusId(id);

        return chatRoom.map(room -> RsData.of(CHATROOM_FOUND, room))
                .orElseGet(() -> RsData.of(CHATROOM_NO_EXISTS));
    }

    public RsData<List<ChatRoom>> findAll() {
        return RsData.of(CHATROOM_FOUND, chatRoomRepository.findAll());
    }

    public RsData<ChatRoom> create(TradeStatus tradeStatus) {
        List<Member> members = new ArrayList<>();
        members.add(tradeStatus.getRequester());
        members.add(tradeStatus.getHelper());

        ChatRoom chatRoom = ChatRoom.builder()
                .id(UUID.randomUUID().toString())
                .tradeStatus(tradeStatus)
                .members(members)
                .createdAt(LocalDateTime.now())
                .build();

        return RsData.of(CHATROOM_CREATED, chatRoomRepository.insert(chatRoom));
    }

    public RsData exit(ChatRoom chatRoom, Member member) {
        List<Member> exitedMembers = chatRoom.getExitedMembers();

        if(exitedMembers.contains(member) || exitedMembers.size() == 2) {
            return RsData.of(CHATROOM_ALREADY_EXITED);
        }

        exitedMembers.add(member);

        ChatRoom chatRoomMemberExited = chatRoomRepository.save(chatRoom.toBuilder()
                .exitedMembers(exitedMembers)
                .build());
        chatRoomRepository.save(chatRoomMemberExited);

        if (chatRoomMemberExited.getExitedMembers().size() == 2) {
            delete(chatRoomMemberExited);
        }

        return RsData.of(CHATROOM_EXITED);
    }

    /**
     * 모든 멤버가 채팅방을 나가면 ChatRoom을 삭제하고 DeletedChatRoom으로 변환하여 저장합니다.
     * 이벤트 리스너를 통해 삭제된 채팅방에 대한 채팅 메시지를 soft-delete합니다.
     */
    public RsData delete(ChatRoom chatRoom) {
        DeletedChatRoom deletedChatRoom = DeletedChatRoom.builder()
                .tradeStatus(chatRoom.getTradeStatus())
                .members(chatRoom.getExitedMembers())
                .build();
        deletedChatRoomRepository.insert(deletedChatRoom);
        chatRoomRepository.delete(chatRoom);

        publisher.publishEvent(new EventAfterDeletedChatRoom(chatRoom));

        return RsData.of(CHATROOM_DELETED);
    }

    public void updateStatusOfChatRoom(ChatRoom chatRoom, Status status) {
        chatRoomRepository.updateStatusOfChatRoom(chatRoom, status.name());
    }

    public void whenAfterDeletedTradeStatus(List<TradeStatus> tradeStatusList) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findByTradeStatus(tradeStatusList);
        for(ChatRoom chatRoom : chatRoomList){
            DeletedChatRoom deletedChatRoom = DeletedChatRoom.builder()
                    .tradeStatus(chatRoom.getTradeStatus())
                    .members(chatRoom.getExitedMembers())
                    .build();
            deletedChatRoomRepository.insert(deletedChatRoom);
            chatRoomRepository.delete(chatRoom);

            publisher.publishEvent(new EventAfterDeletedChatRoom(chatRoom));
        }
    }
}
