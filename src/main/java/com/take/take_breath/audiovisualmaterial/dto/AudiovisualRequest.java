package com.take.take_breath.audiovisualmaterial.dto;

import com.take.take_breath.audiovisualmaterial.AudiovisualMaterial;
import lombok.*;

@Getter
@Setter
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
