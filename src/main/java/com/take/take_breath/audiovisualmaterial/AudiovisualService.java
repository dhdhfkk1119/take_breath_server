package com.take.take_breath.audiovisualmaterial;

import com.take.take_breath.audiovisualmaterial.dto.AudiovisualRequest;
import com.take.take_breath.audiovisualmaterial.dto.AudiovisualResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AudiovisualService {

    private final AudiovisualRepository repository;

    public List<AudiovisualResponse> findAll() {
        return repository.findAll().stream()
                .map(AudiovisualResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AudiovisualResponse findById(Long id) {
        return repository.findById(id)
                .map(AudiovisualResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("해당 자료를 찾을 수 없습니다."));
    }

    public void save(AudiovisualRequest dto) {
        repository.save(dto.toEntity());
    }

    public void update(Long id, AudiovisualRequest dto) {
        AudiovisualMaterial material = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 자료를 찾을 수 없습니다."));
        material.setTitle(dto.getTitle());
        material.setYoutubeUrl(dto.getYoutubeUrl());
        material.setDescription(dto.getDescription());
        material.setDuration(dto.getDuration());
        repository.save(material);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}