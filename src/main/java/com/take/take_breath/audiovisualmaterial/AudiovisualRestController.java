package com.take.take_breath.audiovisualmaterial;

import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.audiovisualmaterial.dto.AudiovisualResponse;
import com.take.take_breath.members.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 플러터 전달용
@RestController
@RequestMapping("/api/audiovisual")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AudiovisualRestController {

    private final AudiovisualService audiovisualService;

    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/list")
    public ResponseEntity<List<AudiovisualResponse>> getAll() {
        return ResponseEntity.ok(audiovisualService.findAll());
    }
}
