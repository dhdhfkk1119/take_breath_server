package com.take.take_breath.members.login;

import com.take.take_breath.members.login.dto.UserInfo;

public interface LoginStrategy {
    String getProvider();
    String getRedirectUrlWithState(String state);
    String getAccessToken(String code, String state);
    UserInfo getUserInfo(String accessToken);
}
