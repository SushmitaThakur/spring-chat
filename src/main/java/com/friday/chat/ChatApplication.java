package com.friday.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) {
		System.out.println("Hello World!");
		SpringApplication.run(ChatApplication.class, args);
	}

}
