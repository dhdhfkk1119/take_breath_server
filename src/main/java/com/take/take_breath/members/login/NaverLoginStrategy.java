package com.take.take_breath.members.login;

import com.take.take_breath.members.login.config.OAuthProperties;
import com.take.take_breath.members.login.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NaverLoginStrategy implements LoginStrategy {

    private final NaverOAuthClient naverOAuthClient;
    private final OAuthProperties oAuthProperties;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getRedirectUrlWithState(String state) {

        return "https://nid.naver.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + oAuthProperties.getClientId()
                + "&redirect_uri=" + oAuthProperties.getRedirectUri()
                + "&state=" + state;
    }

    @Override
    public String getAccessToken(String code, String state) {
        return naverOAuthClient.getAccessToken(code, state);
    }

    @Override
    public UserInfo getUserInfo(String token) {
        return naverOAuthClient.getUserInfo(token);
    }

}
