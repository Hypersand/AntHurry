package com.ant.hurry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableMongoRepositories(basePackages = "com.ant.hurry.chat.repository")
@EnableJpaRepositories(basePackages = {"com.ant.hurry.boundedContext", "com.ant.hurry.base"})
public class HurryApplication {

	public static void main(String[] args) {
		SpringApplication.run(HurryApplication.class, args);
	}

}
