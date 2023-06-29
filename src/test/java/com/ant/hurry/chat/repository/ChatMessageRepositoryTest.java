package com.ant.hurry.chat.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.service.ChatRoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class ChatMessageRepositoryTest {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void refresh() {
        chatMessageRepository.deleteAll();
    }

    @Test
    @DisplayName("repository를 통해 채팅 메시지를 생성하고 저장합니다.")
    void saveByRepository() {
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        ChatRoom chatRoom = chatRoomService
                .create(TradeStatus.builder().requester(member1).helper(member2).build()).getData();
        ChatMessage chatMessage = ChatMessage.builder().chatRoom(chatRoom).build();
        chatMessageRepository.save(chatMessage);

        assertThat(chatMessageRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("MongoTemplate을 통해 채팅 메시지를 생성하고 저장합니다.")
    void saveByMongoTemplate() {
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        ChatRoom chatRoom = chatRoomService
                .create(TradeStatus.builder().requester(member1).helper(member2).build()).getData();
        ChatMessage chatMessage = ChatMessage.builder().chatRoom(chatRoom).build();
        mongoTemplate.save(chatMessage);

        assertThat(mongoTemplate.findAll(ChatMessage.class)).hasSize(1);
    }

    @Test
    @DisplayName("채팅 메시지를 저장한 후 조회하고, 삭제합니다.")
    void save_find_delete() {
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        ChatRoom chatRoom = chatRoomService
                .create(TradeStatus.builder().requester(member1).helper(member2).build()).getData();
        ChatMessage chatMessage = ChatMessage.builder().chatRoom(chatRoom).build();
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        assertThat(chatMessageRepository.findById(chatMessage.getId())).isNotNull();

        chatMessageRepository.delete(savedChatMessage);
        assertThat(chatMessageRepository.findAll()).isEmpty();
    }

}
