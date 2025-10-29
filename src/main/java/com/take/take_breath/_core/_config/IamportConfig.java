package com.take.take_breath._core._config;

import com.siot.IamportRestClient.IamportClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IamportConfig {

    @Value("${iamport.api-key}")
    private String apiKey;

    @Value("${iamport.api-secret}")
    private String apiSecret;

    @Bean
    public IamportClient iamportClient() {
        log.info("포트원 클라이언트 초기화");
        return new IamportClient(apiKey, apiSecret);
    }
}