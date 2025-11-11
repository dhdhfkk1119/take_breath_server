package com.take.take_breath.audiovisualmaterial.dto;

import com.take.take_breath.audiovisualmaterial.AudiovisualMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AudiovisualResponse {
    private Long id;
    private String title;
    private String youtubeUrl;
    private String description;
    private String duration;

    public static AudiovisualResponse fromEntity(AudiovisualMaterial entity) {
        return AudiovisualResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .youtubeUrl(entity.getYoutubeUrl())
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .build();
    }
}
