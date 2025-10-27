package com.take.take_breath.chat.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Getter
public class FileStorageConfig {

    // application.yml에서 설정 (없으면 기본값 사용)
    @Value("${file.upload.path:C:/Users/GGG/Pictures/chat/images}")
    private String uploadPath;

    /**
     * 애플리케이션 시작 시 업로드 디렉토리 생성
     */
    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("✅ 파일 저장 디렉토리 생성: " + uploadPath);
            } else {
                System.out.println("✅ 파일 저장 디렉토리 존재: " + uploadPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 디렉토리 생성 실패: " + uploadPath, e);
        }
    }

    /**
     * 전체 파일 경로 생성
     * @param relativePath 상대 경로 (예: "2025/10/27/uuid.jpg")
     * @return 전체 경로 (예: "C:/Users/GGG/Pictures/chat/images/2025/10/27/uuid.jpg")
     */
    public String getFullPath(String relativePath) {
        return uploadPath + File.separator + relativePath;
    }

}
