package com.take.take_breath.chat;

import com.take.take_breath._core._utils.FileUtil;
import com.take.take_breath.chat.config.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    /**
     * 파일 저장
     * @param file MultipartFile
     * @return 저장된 파일의 상대 경로 (예: "2025/10/27/uuid.jpg")
     */
    public String saveFile(MultipartFile file) throws IOException {
        // 1. 파일 검증
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        // 2. 확장자 검증
        if (!FileUtil.isValidImageExtension(originalFilename)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (jpg, jpeg, png, gif, webp, bmp만 가능)");
        }

        // 3. 상대 경로 생성 (날짜 폴더 + UUID 파일명)
        String relativePath = FileUtil.generateRelativePath(originalFilename);

        // 4. 전체 경로 생성
        String fullPath = fileStorageConfig.getFullPath(relativePath);
        Path targetPath = Paths.get(fullPath);

        // 5. 디렉토리 생성 (날짜별 폴더)
        Files.createDirectories(targetPath.getParent());

        // 6. 파일 저장
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("✅ 파일 저장 완료: " + fullPath);

        // 7. 상대 경로 반환 (DB에 저장할 값)
        return relativePath;
    }

    /**
     * 파일 읽기
     * @param relativePath 상대 경로 (예: "2025/10/27/uuid.jpg")
     * @return 파일 데이터 (byte[])
     */
    public byte[] loadFile(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isEmpty()) {
            throw new IllegalArgumentException("파일 경로가 없습니다.");
        }

        String fullPath = fileStorageConfig.getFullPath(relativePath);
        Path filePath = Paths.get(fullPath);

        if (!Files.exists(filePath)) {
            throw new IOException("파일을 찾을 수 없습니다: " + relativePath);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * 파일 삭제
     * @param relativePath 상대 경로 (예: "2025/10/27/uuid.jpg")
     */
    public void deleteFile(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        String fullPath = fileStorageConfig.getFullPath(relativePath);
        Path filePath = Paths.get(fullPath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("✅ 파일 삭제 완료: " + fullPath);
        }
    }

    /**
     * 파일 존재 여부 확인
     * @param relativePath 상대 경로
     * @return 존재하면 true
     */
    public boolean fileExists(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        String fullPath = fileStorageConfig.getFullPath(relativePath);
        return Files.exists(Paths.get(fullPath));
    }

}
