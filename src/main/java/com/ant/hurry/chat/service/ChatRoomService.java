package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.entity.DeletedChatRoom;
import com.ant.hurry.chat.event.EventAfterDeletedChatRoom;
import com.ant.hurry.chat.repository.ChatRoomRepository;
import com.ant.hurry.chat.repository.DeletedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ant.hurry.chat.code.ChatRoomErrorCode.CHATROOM_NO_EXISTS;
import static com.ant.hurry.chat.code.ChatRoomSuccessCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final DeletedChatRoomRepository deletedChatRoomRepository;
    private final ApplicationEventPublisher publisher;

    public RsData<ChatRoom> findById(String id) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);

        if (chatRoom.isEmpty()) {
            return RsData.of(CHATROOM_NO_EXISTS);
        }
        ChatRoom foundchatRoom = chatRoom.get();

        return RsData.of(CHATROOM_FOUND, foundchatRoom);
    }

    public RsData<List<ChatRoom>> findByMember(Member member) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(member);
        return RsData.of(CHATROOM_FOUND, chatRooms);
    }

    public RsData<List<ChatRoom>> findAll() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return RsData.of(CHATROOM_FOUND, chatRooms);
    }

    @Transactional
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
        ChatRoom insertChatRoom = chatRoomRepository.insert(chatRoom);

        return RsData.of(CHATROOM_CREATED, insertChatRoom);
    }

    @Transactional
    public RsData exit(ChatRoom chatRoom, Member member) {
        List<Member> members = chatRoom.getMembers();
        members.remove(member);
        List<Member> exitedMembers = chatRoom.getExitedMembers();
        exitedMembers.add(member);

        ChatRoom chatRoomMemberExited = chatRoom.toBuilder()
                .members(members)
                .exitedMembers(exitedMembers)
                .build();
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
    @Transactional
    public RsData delete(ChatRoom chatRoom) {
        DeletedChatRoom deletedChatRoom = DeletedChatRoom.builder()
                .tradeStatus(chatRoom.getTradeStatus())
                .members(chatRoom.getExitedMembers())
                .build();
        deletedChatRoomRepository.insert(deletedChatRoom);
        chatRoomRepository.delete(chatRoom);

        publisher.publishEvent(new EventAfterDeletedChatRoom(this, chatRoom));

        return RsData.of(CHATROOM_DELETED);
    }

}
