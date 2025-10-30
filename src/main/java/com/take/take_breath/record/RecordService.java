package com.take.take_breath.record;

import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath._core._utils.FileUtil;
import com.take.take_breath._core._utils.UploadFile;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.record.dto.RecordListResponse;
import com.take.take_breath.record.dto.RecordResponse;
import com.take.take_breath.record.dto.RecordSaveRequest;
import com.take.take_breath.record.dto.UploadedFileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private final UploadFile uploadFile;

    private final String commonSavedPath = "/uploads/record/";

    /**
     * 특정 사용자의 기록 목록 조회(페이징)
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<RecordListResponse> getRecordList(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recordRepository.findByMemberEmail(email, pageable).map(RecordListResponse::fromEntity);
    }
    
    /**
     * 특정 기록 상세 조회
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public RecordResponse getRecord(Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 기록이 존재하지 않습니다"));
        return RecordResponse.fromEntity(record);
    }

    /**
     * 기록 저장
     * @param email
     * @param request
     * @return
     */
    public Long saveRecord(String email, RecordSaveRequest request) throws IOException {
        // 작성자 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        // Record 엔티티 생성
        Record record = Record.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .recordDate(request.getRecordDate())
                .build();

        // Record 저장 (ID 생성을 위해 먼저 저장)
        Record savedRecord = recordRepository.save(record);

        // 이미지 파일 처리
        if (request.getImageFiles() != null && !request.getImageFiles().isEmpty()) {
            List<UploadedFileInfo> uploadedImages = uploadFile.uploadRecordImages(request.getImageFiles());

            for (UploadedFileInfo fileInfo : uploadedImages) {
                RecordFile recordFile = RecordFile.builder()
                        .record(savedRecord)
                        .fileType(FileType.IMAGE)
                        .fileName(fileInfo.getSavedFileName())
                        .originalFileName(fileInfo.getOriginalFileName())
                        .filePath(fileInfo.getFilePath())
                        .fileSize(fileInfo.getFileSize())
                        .contentType(fileInfo.getContentType())
                        .build();

                savedRecord.addRecordFile(recordFile);  // 양방향 연관관계 설정
            }
        }

        // 오디오 파일 처리
        if (request.getAudioFiles() != null && !request.getAudioFiles().isEmpty()) {
            List<UploadedFileInfo> uploadedAudios = uploadFile.uploadRecordAudios(request.getAudioFiles());

            for (UploadedFileInfo fileInfo : uploadedAudios) {
                RecordFile recordFile = RecordFile.builder()
                        .record(savedRecord)
                        .fileType(FileType.AUDIO)
                        .fileName(fileInfo.getSavedFileName())
                        .originalFileName(fileInfo.getOriginalFileName())
                        .filePath(fileInfo.getFilePath())
                        .fileSize(fileInfo.getFileSize())
                        .contentType(fileInfo.getContentType())
                        .build();

                savedRecord.addRecordFile(recordFile);
            }
        }

        // 비디오 파일 처리
        if (request.getVideoFiles() != null && !request.getVideoFiles().isEmpty()) {
            List<UploadedFileInfo> uploadedVideos = uploadFile.uploadRecordVideos(request.getVideoFiles());

            for (UploadedFileInfo fileInfo : uploadedVideos) {
                RecordFile recordFile = RecordFile.builder()
                        .record(savedRecord)
                        .fileType(FileType.VIDEO)
                        .fileName(fileInfo.getSavedFileName())
                        .originalFileName(fileInfo.getOriginalFileName())
                        .filePath(fileInfo.getFilePath())
                        .fileSize(fileInfo.getFileSize())
                        .contentType(fileInfo.getContentType())
                        .build();

                savedRecord.addRecordFile(recordFile);
            }
        }

        return savedRecord.getId();
    }



}
