package com.take.take_breath._core._utils;

import com.take.take_breath.record.dto.UploadedFileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UploadFile {

    private final UploadProperties uploadProperties;

    /**
     * 단일 이미지 업로드
     * @param file 업로드할 파일
     * @param dirType "member" 또는 "counselor" + "chat"
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
     * @param dirType "member" 또는 "counselor"
     */
    public void deleteProfileImage(String imagePath, String dirType) {
        if (imagePath == null || imagePath.isBlank()) return;

        try {
            if (imagePath.contains("default_profile.png")) return; // 기본 이미지 삭제 방지

            // URL이면 접두사 제거
            if (imagePath.startsWith("http")) {
                int idx = imagePath.indexOf("/static/uploads/");
                if (idx != -1) {
                    imagePath = imagePath.substring(idx + "/static/uploads/".length());
                }
            }

            Path filePath = Paths.get(uploadProperties.getRootDir(), imagePath);
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지를 삭제하지 못했습니다", e);
        }
    }


    /**
     * Record 이미지 파일들 업로드 (다중 파일)
     * @param files 업로드할 이미지 파일 리스트
     * @return 저장된 파일 정보 리스트 (UploadedFileInfo)
     */
    public List<UploadedFileInfo> uploadRecordImages(List<MultipartFile> files) throws IOException {
        return uploadRecordFiles(files, uploadProperties.getRecordImageDir());
    }

    /**
     * Record 오디오 파일들 업로드 (다중 파일)
     * @param files 업로드할 오디오 파일 리스트
     * @return 저장된 파일 정보 리스트 (UploadedFileInfo)
     */
    public List<UploadedFileInfo> uploadRecordAudios(List<MultipartFile> files) throws IOException {
        return uploadRecordFiles(files, uploadProperties.getRecordAudioDir());
    }

    /**
     * Record 비디오 파일들 업로드 (다중 파일)
     * @param files 업로드할 비디오 파일 리스트
     * @return 저장된 파일 정보 리스트 (UploadedFileInfo)
     */
    public List<UploadedFileInfo> uploadRecordVideos(List<MultipartFile> files) throws IOException {
        return uploadRecordFiles(files, uploadProperties.getRecordVideoDir());
    }

    /**
     * Record 파일들 업로드 공통 로직
     * @param files 업로드할 파일 리스트
     * @param subDir 하위 디렉터리 (records/images/, records/audio/, records/videos/)
     * @return 저장된 파일 정보 리스트
     */
    private List<UploadedFileInfo> uploadRecordFiles(List<MultipartFile> files, String subDir) throws IOException {
        List<UploadedFileInfo> uploadedFiles = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            return uploadedFiles;
        }

        // 전체 업로드 경로: ./uploads/records/images/
        String fullUploadPath = Paths.get(uploadProperties.getRootDir(), subDir).toString();

        // 디렉터리 없으면 생성
        createUploadDirectory(fullUploadPath);

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // 원본 파일명
            String originalFileName = file.getOriginalFilename();

            // 파일 확장자 추출
            String ext = getFileExtension(originalFileName);

            // 유니크 파일명 생성
            String savedFileName = generateUniqueFileName(ext);

            // 저장 경로
            Path savePath = Paths.get(fullUploadPath, savedFileName);

            // 파일 저장
            file.transferTo(savePath);

            // DB 저장용 상대 경로 (예: records/images/20251027_120000_abc123.png)
            // String relativePath = Paths.get(subDir, savedFileName).toString().replace("\\", "/");
            // String relativePath = Paths.get(fullUploadPath, savedFileName).toString().replace("\\", "/");
            String relativePath = savePath.toString().replace("\\", "/");

            // 파일 정보 객체 생성
            UploadedFileInfo fileInfo = UploadedFileInfo.builder()
                    .originalFileName(originalFileName)
                    .savedFileName(savedFileName)
                    .filePath(relativePath)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build();

            uploadedFiles.add(fileInfo);
        }

        return uploadedFiles;
    }

    /**
     * 파일 경로 리스트로 파일 삭제
     *
     * @param filePaths 삭제할 파일 경로 리스트
     */
    public void deleteFiles(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (String filePath : filePaths) {
            if (deleteFile(filePath)) {
                successCount++;
            } else {
                failCount++;
            }
        }
    }

    /**
     * 단일 파일 삭제 (파일시스템)
     * @param relativePath DB에 저장된 상대 경로 (예: records/images/xxx.png)
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        try {
            Path filePath = Paths.get(uploadProperties.getRootDir(), relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            return false;
        }
    }


    /**
     * 채팅 이미지 읽기
     * @param relativePath DB에 저장된 상대 경로 (예: chat-image/uuid.jpg)
     * @return 파일 데이터 (byte[])
     */
    public byte[] loadChatImage(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isEmpty()) {
            throw new IllegalArgumentException("파일 경로가 없습니다.");
        }

        // 전체 경로: ./uploads/chat-image/uuid.jpg
        String fullPath = Paths.get(uploadProperties.getRootDir(), relativePath).toString();
        Path filePath = Paths.get(fullPath);

        if (!Files.exists(filePath)) {
            throw new IOException("파일을 찾을 수 없습니다: " + relativePath);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * 채팅 이미지 삭제
     * @param relativePath DB에 저장된 상대 경로 (예: chat-image/uuid.jpg)
     */
    public void deleteChatImage(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        try {
            String fullPath = Paths.get(uploadProperties.getRootDir(), relativePath).toString();
            Path filePath = Paths.get(fullPath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("✅ 채팅 이미지 삭제 완료: " + fullPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("채팅 이미지를 삭제하지 못했습니다", e);
        }
    }


    /**
     * 디렉터리 타입 구분 (yml 기준)
     */
    private String resolveDirectory(String dirType) {
        if ("member".equalsIgnoreCase(dirType)) {
            return uploadProperties.getMemberDir();
        } else if ("counselor".equalsIgnoreCase(dirType)) {
            return uploadProperties.getCounselorDir();
        } else if("chat".equalsIgnoreCase(dirType)) {
            return uploadProperties.getChatImageDir();
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
