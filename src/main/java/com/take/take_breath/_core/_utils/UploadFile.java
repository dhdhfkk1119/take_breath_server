package com.take.take_breath._core._utils;

import com.take.take_breath._core._utils.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadFile {

    private final UploadProperties uploadProperties;

    /**
     * 단일 이미지 업로드
     * @param file 업로드할 파일
     * @param dirType "member" 또는 "corp"
     * @return 저장된 파일 경로 (예: member-images/20251027_abc123.png)
     */
    public String uploadImage(MultipartFile file, String dirType) throws IOException {
        // 디렉터리 구분 (yml 설정에서 가져옴)
        String subDir = resolveDirectory(dirType);

        // 전체 업로드 경로: ./uploads/member-images/
        String fullUploadPath = Paths.get(uploadProperties.getRootDir(), subDir).toString();

        // 디렉터리 없으면 생성
        createUploadDirectory(fullUploadPath);

        // 파일 확장자 및 유니크 이름 생성
        String ext = getFileExtension(file.getOriginalFilename());
        String uniqueName = generateUniqueFileName(ext);

        // 저장 경로
        Path savePath = Paths.get(fullUploadPath, uniqueName);
        file.transferTo(savePath);

        // DB에는 root 제외한 상대경로로 저장
        // 예: member-images/20251027_abc123.png
        return Paths.get(subDir, uniqueName).toString().replace("\\", "/");
    }

    /**
     * 프로필 이미지 삭제
     * @param imagePath DB에 저장된 상대경로 (ex. member-images/xxx.png)
     * @param dirType "member" 또는 "corp"
     */
    public void deleteProfileImage(String imagePath, String dirType) {
        if (imagePath == null || imagePath.isBlank()) return;

        try {
            if (imagePath.contains("default_profile.png")) return; // 기본 이미지 삭제 방지

            // URL이면 접두사 제거
            if (imagePath.startsWith("http")) {
                int idx = imagePath.indexOf("/uploads/");
                if (idx != -1) {
                    imagePath = imagePath.substring(idx + "/uploads/".length());
                }
            }

            Path filePath = Paths.get(uploadProperties.getRootDir(), imagePath);
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지를 삭제하지 못했습니다", e);
        }
    }

    /**
     * 디렉터리 타입 구분 (yml 기준)
     */
    private String resolveDirectory(String dirType) {
        if ("member".equalsIgnoreCase(dirType)) {
            return uploadProperties.getMemberDir();
        } else if ("corp".equalsIgnoreCase(dirType)) {
            return uploadProperties.getCorpDir();
        }
        throw new IllegalArgumentException("잘못된 디렉터리 타입입니다: " + dirType);
    }

    /**
     * 파일명 생성 (예: 20251027_120000_abcdef12.png)
     */
    private String generateUniqueFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + extension;
    }

    /**
     * 확장자 추출
     */
    private String getFileExtension(String originFilename) {
        if (originFilename == null) return "";
        int idx = originFilename.lastIndexOf('.');
        return (idx == -1) ? "" : originFilename.substring(idx);
    }

    /**
     * 업로드 디렉터리 없으면 생성
     */
    private void createUploadDirectory(String fullUploadPath) throws IOException {
        Path uploadPath = Paths.get(fullUploadPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }
}
