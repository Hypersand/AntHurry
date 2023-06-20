package com.ant.hurry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HurryApplication {

	public static void main(String[] args) {
		SpringApplication.run(HurryApplication.class, args);
	}

}
