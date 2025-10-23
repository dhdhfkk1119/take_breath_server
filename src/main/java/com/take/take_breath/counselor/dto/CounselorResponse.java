package com.take.take_breath.counselor.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselorResponse {
    private Long id;
    private String name;
    private String license;
    private String specialty;
    private String introduction;
    private String profileImage;
    private String hashtags;
    private int price;
}
