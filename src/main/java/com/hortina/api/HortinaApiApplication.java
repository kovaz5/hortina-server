package com.hortina.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HortinaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HortinaApiApplication.class, args);
	}

}