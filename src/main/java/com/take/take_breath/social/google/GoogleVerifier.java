package com.take.take_breath.social.google;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.take.take_breath.members.login.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleVerifier {

    public UserInfo verify(String idToken) {
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

            return new UserInfo(
                    "google",                 // provider
                    decoded.getUid(),        // socialId (Google 고유 ID → Firebase UID)
                    decoded.getEmail(),      // email
                    decoded.getName(),       // name
                    decoded.getPicture()     // profileImage
            );
        } catch (Exception e) {
            throw new RuntimeException("Google ID Token 검증 실패", e);
        }
    }
}
