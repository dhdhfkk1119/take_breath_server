package com.take.take_breath.social.google;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseInitializer {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        log.info("Initializing Firebase.");

        // resources 디렉토리에서 파일을 읽어옵니다.
        InputStream serviceAccount = new ClassPathResource("firebase.json").getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("takeabreath-72320.appspot.com")
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);
        log.info("FirebaseApp initialized" + app.getName());
        return app;
    }


    @Bean
    public FirebaseAuth getFirebaseAuth(){
        FirebaseAuth firebaseAuth = null;
        try {
            firebaseAuth = FirebaseAuth.getInstance(firebaseApp());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return firebaseAuth;
    }
}