package com.take.take_breath.members.login;

import com.take.take_breath.members.login.config.OAuthProperties;
import com.take.take_breath.members.login.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NaverOAuthClient {

    private final OAuthProperties oAuthProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken(String code, String state) {

        String url = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", oAuthProperties.getClientId());
        params.add("client_secret", oAuthProperties.getClientSecret());
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(params, headers);

        ResponseEntity<Map> res = restTemplate.postForEntity(url, req, Map.class);
        return (String) res.getBody().get("access_token");
    }

    public UserInfo getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> res = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> body = (Map<String, Object>) res.getBody().get("response");

        return new UserInfo(
                "naver",
                (String) body.get("id"),               // socialId
                (String) body.get("email"),
                (String) body.get("name"),
                (String) body.get("profile_image")
        );
    }
}