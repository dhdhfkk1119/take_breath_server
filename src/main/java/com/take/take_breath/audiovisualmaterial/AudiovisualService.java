package com.take.take_breath.audiovisualmaterial;

import com.take.take_breath.audiovisualmaterial.dto.AudiovisualRequest;
import com.take.take_breath.audiovisualmaterial.dto.AudiovisualResponse;
import com.take.take_breath.audiovisualmaterial.dto.YoutubeVideoInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AudiovisualService {

    private final AudiovisualRepository repository;
    private final YoutubeClient youtubeClient;

    /** 전체 목록 조회 */
    public List<AudiovisualResponse> findAll() {
        return repository.findAll().stream()
                .map(AudiovisualResponse::fromEntity)
                .toList();
    }

    /** 단건 조회 */
    public AudiovisualResponse findById(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 자료가 존재하지 않습니다."));
        return AudiovisualResponse.fromEntity(entity);
    }

    /** 등록 */
    public void save(AudiovisualRequest dto) {
        var entity = dto.toEntity();

        if (StringUtils.hasText(entity.getYoutubeUrl())) {
            YoutubeVideoInfo info = youtubeClient.fetchVideoInfo(entity.getYoutubeUrl());

            if (info != null) {
                if (!StringUtils.hasText(entity.getTitle())) {
                    entity.setTitle(info.getTitle());
                }
                if (!StringUtils.hasText(entity.getDescription())) {
                    entity.setDescription(info.getDescription());
                }
                if (!StringUtils.hasText(entity.getDuration())) {
                    entity.setDuration(info.getDuration());
                }
            }
        }

        repository.save(entity);
    }

    /** 수정 */
    public void update(Long id, AudiovisualRequest dto) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 자료가 존재하지 않습니다."));

        boolean urlChanged = dto.getYoutubeUrl() != null && !dto.getYoutubeUrl().equals(entity.getYoutubeUrl());

        entity.setTitle(dto.getTitle());
        entity.setYoutubeUrl(dto.getYoutubeUrl());
        entity.setDescription(dto.getDescription());

        if (StringUtils.hasText(dto.getDuration())) {
            entity.setDuration(dto.getDuration());
        } else if (urlChanged && StringUtils.hasText(dto.getYoutubeUrl())) {
            YoutubeVideoInfo info = youtubeClient.fetchVideoInfo(dto.getYoutubeUrl());
            if (info != null) {
                entity.setDuration(info.getDuration());
                if (!StringUtils.hasText(dto.getTitle())) {
                    entity.setTitle(info.getTitle());
                }
                if (!StringUtils.hasText(dto.getDescription())) {
                    entity.setDescription(info.getDescription());
                }
            }
        }

        repository.save(entity);
    }

    /** 삭제 */
    public void delete(Long id) {
        repository.deleteById(id);
    }
}