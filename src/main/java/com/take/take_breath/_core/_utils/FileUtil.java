package com.take.take_breath._core._utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 파일 관련 유틸리티
 */
public class FileUtil {

    // 허용되는 이미지 확장자
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    /**
     * UUID 기반 파일명 생성
     * @param originalFilename 원본 파일명 (예: "강아지.jpg")
     * @return UUID + 확장자 (예: "a3f5b2c1-4d8e-4f1a-9c3b-1e5f6a7b8c9d.jpg")
     */
    public static String generateFilename(String originalFilename) {
        String extension = getExtension(originalFilename);
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * 확장자 추출
     * @param filename 파일명
     * @return 확장자 (예: ".jpg")
     */
    public static String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf(".");
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot).toLowerCase();
    }

    /**
     * 이미지 파일 확장자 검증
     * @param filename 파일명
     * @return 유효하면 true
     */
    public static boolean isValidImageExtension(String filename) {
        String extension = getExtension(filename);
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * 날짜별 폴더 경로 생성
     * @return 날짜 경로 (예: "2025/10/27")
     */
    public static String getDatePath() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return now.format(formatter);
    }

    /**
     * 상대 경로 생성 (날짜 폴더 + UUID 파일명)
     * @param originalFilename 원본 파일명
     * @return 상대 경로 (예: "2025/10/27/uuid.jpg")
     */
    public static String generateRelativePath(String originalFilename) {
        String datePath = getDatePath();
        String filename = generateFilename(originalFilename);
        return datePath + "/" + filename;
    }

    /**
     * 파일 크기를 읽기 쉬운 형식으로 변환
     * @param size 바이트 단위 크기
     * @return 읽기 쉬운 형식 (예: "1.5 MB")
     */
    public static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
}