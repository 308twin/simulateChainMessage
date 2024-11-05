package com.fdu.chainmessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChainmessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChainmessageApplication.class, args);
	}

}
