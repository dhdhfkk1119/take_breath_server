package com.take.take_breath.audiovisualmaterial.dto;

import com.take.take_breath.audiovisualmaterial.AudiovisualMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudiovisualRequest {
    private String title;
    private String youtubeUrl;
    private String description;
    private String duration;

    public AudiovisualMaterial toEntity() {
        return AudiovisualMaterial.builder()
                .title(title)
                .youtubeUrl(youtubeUrl)
                .description(description)
                .duration(duration)
                .build();
    }
}
