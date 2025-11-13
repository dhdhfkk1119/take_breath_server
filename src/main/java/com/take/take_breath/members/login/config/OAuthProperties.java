package com.take.take_breath.members.login.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "naver")
public class OAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}