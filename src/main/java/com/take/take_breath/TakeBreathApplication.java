package com.take.take_breath;

import com.take.take_breath.members.login.config.OAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(OAuthProperties.class)
public class TakeBreathApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeBreathApplication.class, args);
	}

}
