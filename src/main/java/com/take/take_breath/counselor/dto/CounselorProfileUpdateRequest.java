package com.take.take_breath.counselor.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CounselorProfileUpdateRequest {
    private String introduction;
    private MultipartFile profileImage;
}
