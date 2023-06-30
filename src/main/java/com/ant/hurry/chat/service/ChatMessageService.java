package com.ant.hurry.chat.service;

import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.chat.config.MongoConfig;
import com.ant.hurry.chat.dto.ChatMessageDto;
import com.ant.hurry.chat.entity.ChatFileMessage;
import com.ant.hurry.chat.entity.ChatMessage;
import com.ant.hurry.chat.entity.ChatRoom;
import com.ant.hurry.chat.repository.ChatFileMessageRepository;
import com.ant.hurry.chat.repository.ChatMessageRepository;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ant.hurry.chat.code.ChatMessageErrorCode.*;
import static com.ant.hurry.chat.code.ChatMessageSuccessCode.*;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatFileMessageRepository chatFileMessageRepository;
    private final MongoConfig mongoConfig;
    private final ResourceLoader resourceLoader;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public RsData<ChatFileMessage> findFileMessageById(String id) {
        Optional<ChatFileMessage> message = chatFileMessageRepository.findById(id);
        return message.map(chatFileMessage -> RsData.of(MESSAGE_FOUND, chatFileMessage))
                .orElseGet(() -> RsData.of(MESSAGE_NOT_EXISTS));
    }

    public RsData<List<ChatMessage>> findByChatRoom(ChatRoom chatRoom) {
        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessageByChatRoom(chatRoom);
        return RsData.of(MESSAGE_FOUND, chatMessages);
    }

    // 일반 메시지 전송
    public RsData<ChatMessage> send(ChatMessageDto dto) {
        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .chatRoom(dto.getChatRoom())
                .sender(dto.getSender())
                .content(dto.getContent())
                .build();
        chatMessageRepository.save(message);
        message.getChatRoom().setLatestMessage(message);
        return RsData.of(MESSAGE_SENT, message);
    }

    // 파일 메시지 전송
    public RsData<ChatFileMessage> sendFile(MultipartFile file, Member sender, ChatRoom chatRoom) throws IOException {
        if (file.isEmpty()) {
            return RsData.of(FILE_NOT_EXISTS);
        }
        if(file.getSize() > 10 * 1024 * 1024) {
            return RsData.of(FILE_TOO_BIG);
        }

        GridFSBucket gridBucket =
                GridFSBuckets.create(mongoConfig.mongoClient().getDatabase(databaseName));

        // 파일 업로드
        ObjectId fileId = saveFile(file).getData();

        // 업로드한 파일 조회
        GridFSFile uploadedFile = gridBucket.find(Filters.eq("_id", fileId)).first();

        // 파일의 경로 생성
        String filePath = String.format("%s/%s", gridBucket.getBucketName(), uploadedFile.getObjectId());

        ChatFileMessage chatFileMessage = ChatFileMessage.builder()
                .id(UUID.randomUUID().toString())
                .uploadFilePath(filePath)
                .uploadFileId(fileId.toString())
                .chatRoom(chatRoom)
                .sender(sender)
                .createdAt(LocalDateTime.now())
                .build();
        chatFileMessageRepository.insert(chatFileMessage);

        return RsData.of(MESSAGE_SENT, chatFileMessage);
    }

    // 파일 업로드(저장)
    private RsData<ObjectId> saveFile(MultipartFile file) throws IOException {
        Document doc = new Document()
                .append("type", "file")
                .append("content_type", file.getContentType())
                .append("id", UUID.randomUUID().toString())
                .append("date", LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        InputStream inputStream = file.getInputStream();

        GridFSBucket gridBucket =
                GridFSBuckets.create(mongoConfig.mongoClient().getDatabase(databaseName));

        GridFSUploadOptions uploadOptions = new GridFSUploadOptions().chunkSizeBytes(1024).metadata(doc);
        ObjectId fileId = gridBucket.uploadFromStream(file.getOriginalFilename(), inputStream, uploadOptions);
        inputStream.close();

        return RsData.of(FILE_SAVED, fileId);
    }

    // 파일 다운로드
    public ResponseEntity<StreamingResponseBody> downloadFile(ChatFileMessage message) throws IOException {
        String fileId = message.getUploadFileId();

        GridFSBucket gridBucket =
                GridFSBuckets.create(mongoConfig.mongoClient().getDatabase(databaseName));

        GridFSFile file = gridBucket.find(Filters.eq("_id", new ObjectId(fileId))).first();

        if(file == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMetadata().getString("content_type")));
        headers.setContentLength(file.getLength());
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getFilename()).build());

        StreamingResponseBody responseBody = outputStream -> {
            GridFSDownloadStream downloadStream = gridBucket.openDownloadStream(file.getObjectId());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = downloadStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            downloadStream.close();
            outputStream.close();
        };

        return ResponseEntity.ok().headers(headers).body(responseBody);
    }

    public RsData deleteSoft(ChatMessage chatMessage) {
        ChatMessage deletedChatMessage = chatMessageRepository.deleteSoft(chatMessage);

        return deletedChatMessage.getDeletedAt() != null ?
                RsData.of(MESSAGE_DELETED) : RsData.of(MESSAGE_NOT_DELETED);
    }

    public RsData delete(ChatMessage message) {
        chatMessageRepository.delete(message);
        return RsData.of(MESSAGE_DELETED);
    }

    public void whenAfterDeletedChatRoom(ChatRoom chatRoom) {
        List<ChatMessage> chatMessages = findByChatRoom(chatRoom).getData();
        chatMessages.forEach(chatMessageRepository::deleteSoft);
    }

}
