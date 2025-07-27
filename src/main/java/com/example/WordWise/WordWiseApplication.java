package com.example.WordWise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WordWiseApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordWiseApplication.class, args);
	}

}
