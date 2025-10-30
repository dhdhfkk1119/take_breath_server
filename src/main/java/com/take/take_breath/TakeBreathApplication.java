package com.take.take_breath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class TakeBreathApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeBreathApplication.class, args);
	}

}
