package com.ant.hurry.base.initData;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeMongoDb implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        List<String> collections = List.of(
                "chat_file", "chat_message", "chat_room", "deleted_room", "fs.chunks", "fs.files", "latest_message"
        );

        for (String collection : collections) {
            if (mongoTemplate.collectionExists(collection)) {
                mongoTemplate.dropCollection(collection);
                mongoTemplate.createCollection(collection);
            }
        }
    }
}
