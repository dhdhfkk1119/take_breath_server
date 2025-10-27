package com.take.take_breath.chat.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ImageUploadRequest {
    private Long chatRoomId;
    private Long senderId;
    private MultipartFile image;  // 이미지 파일
}