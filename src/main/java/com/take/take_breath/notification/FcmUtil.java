//package com.take.take_breath.notification;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class FcmUtil {
//
//    private static final String PROJECT_ID = "takeabreath-72320";
//    private static final String FCM_API_URL =
//            "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send";
//
//    public void sendMessage(String fcmToken, String title, String body) throws IOException, IOException {
//        // AccessToken 생성
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource("firebase/service-account.json").getInputStream())
//                .createScoped("https://www.googleapis.com/auth/firebase.messaging");
//        googleCredentials.refreshIfExpired();
//        String accessToken = googleCredentials.getAccessToken().getTokenValue();
//
//        // 메시지 payload 구성
//        Map<String, Object> message = new HashMap<>();
//        Map<String, Object> notification = new HashMap<>();
//        notification.put("title", title);
//        notification.put("body", body);
//
//        Map<String, Object> messageContent = new HashMap<>();
//        messageContent.put("token", fcmToken);
//        messageContent.put("notification", notification);
//
//        message.put("message", messageContent);
//
//        // HTTP 요청
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(accessToken);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(message, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response =
//                restTemplate.exchange(FCM_API_URL, HttpMethod.POST, entity, String.class);
//
//        System.out.println("FCM Response: " + response.getBody());
//    }
//}
//
