package com.take.take_breath.social.naver;

import com.take.take_breath.members.login.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverVerifier {
    private final RestTemplate restTemplate = new RestTemplate();

    public UserInfo verify(String accessToken) {

        String url = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
        );

        Map responseBody = (Map) response.getBody().get("response");

        return new UserInfo(
                (String) responseBody.get("id"),               // uid
                (String) responseBody.get("email"),            // email
                (String) responseBody.get("name"),             // name
                (String) responseBody.get("profile_image"),    // profileImage
                "naver"                                        // provider
        );

    }
}
