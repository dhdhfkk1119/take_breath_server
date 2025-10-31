package com.take.take_breath.record;

import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath._core._exception.Exception500;
import com.take.take_breath._core._utils.FileUtil;
import com.take.take_breath._core._utils.UploadFile;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.record.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final RecordFileRepository recordFileRepository;
    private final MemberRepository memberRepository;
    private final UploadFile uploadFile;

    private final String commonSavedPath = "/uploads/record/";

    /**
     * 특정 사용자의 기록 목록 조회(페이징)
     *
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
     *
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
     *
     * @param email
     * @param request
     * @return
     */
    public Record saveRecord(String email, RecordSaveRequest request) {
        // 사용자 검증
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        // Record 엔티티 생성
        Record record = Record.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();

        Record savedRecord = recordRepository.save(record);

        // 이미지 파일 처리
        try {
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
        } catch (IOException e) {
            throw new Exception500("파일 저장 중 오류 발생");
        }

        return record;
    }

    /**
     * 기록 수정 - 수정된 파일 처리 방법 필요
     */
    public Record updateRecord(String email, Long recordId, RecordUpdateRequest request) {
        // 예외 처리 & 권한 체크
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new Exception404("해당 기록이 존재하지 않습니다."));
        if (!record.getMember().getId().equals(member.getId())) {
            throw new Exception403("해당 기록을 수정할 권한이 없습니다");
        }

        // 기본 필드 업데이트
        record.setTitle(request.getTitle());
        record.setContent(request.getContent());

        // 기존 파일 목록 조회
        List<RecordFile> existingImages = recordFileRepository.findByRecordIdAndFileType(recordId, FileType.IMAGE);
        List<RecordFile> existingAudios = recordFileRepository.findByRecordIdAndFileType(recordId, FileType.AUDIO);
        List<RecordFile> existingVideos = recordFileRepository.findByRecordIdAndFileType(recordId, FileType.VIDEO);

        // 이미지 처리

        try {
            // 이미지 처리
            handleFileUpdate(record, existingImages, request.getImageFiles(), FileType.IMAGE);
            // 오디오 처리
            handleFileUpdate(record, existingAudios, request.getAudioFiles(), FileType.AUDIO);
            // 비디오 처리
            handleFileUpdate(record, existingVideos, request.getVideoFiles(), FileType.VIDEO);
        } catch (IOException e) {
            throw new Exception500("파일 저장 중 오류 발생");
        }
        
        return recordRepository.save(record);
    }

    /**
     * 기록 삭제
     *
     * @param recordId
     * @return
     */
    public void deleteRecord(String email, Long recordId) {
        // 예외 처리
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new Exception404("현재 존재하지 않는 기록입니다"));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("현재 존재하지 않는 사용자입니다"));

        // 권한 처리
        if (!record.getMember().getId().equals(member.getId())) {
            throw new Exception403("현재 사용자가 삭제할 권한을 가지고 있지 않습니다.");
        }

        recordRepository.delete(record);
    }


    /**
     * 파일 처리 및 저장
     *
     * @param record   연결될 Record 엔티티
     * @param files    업로드된 파일 목록
     * @param fileType 파일 타입 (IMAGE, AUDIO, VIDEO)
     */
    private void processFiles(Record record, List<MultipartFile> files, FileType fileType) {
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                // 파일 원본이름, 확장자
                String originalFileName = file.getOriginalFilename();
                String savedFileName = FileUtil.generateSavedFilename(originalFileName);
                String filePath = commonSavedPath + fileType.getDirectory() + "/" + savedFileName;
                Long fileSize = file.getSize();
                String contentType = file.getContentType();

                // RecordFile 엔티티 생성
                RecordFile recordFile = RecordFile.builder()
                        .record(record)
                        .fileType(fileType)
                        .fileName(savedFileName)
                        .originalFileName(originalFileName)
                        .filePath(filePath)
                        .fileSize(fileSize)
                        .contentType(contentType)
                        .build();

                // RecordFile을 Record의 files 리스트에 추가
                record.getRecordFiles().add(recordFile);
            } catch (Exception e) {
                throw new Exception500("파일 업로드 중 오류가 발생했습니다");
            }
        }
    }

    /**
     * 파일 업데이트 처리
     */
    private void handleFileUpdate(Record record, List<RecordFile> existingFiles, List<MultipartFile> newFiles, FileType fileType) throws IOException {
        // 새 파일이 없는 경우 -> 기존 그대로 유지
        if(newFiles == null) return;

        // 기존 파일 중에서 요청에 없는 것 제거 -> DB, 디렉터리 둘다 제거
        for(RecordFile oldFile : existingFiles) {
            boolean stillExists = newFiles.stream()
                    .anyMatch(file -> file.getOriginalFilename().equals(oldFile.getOriginalFileName()));
            if(!stillExists) {
                Files.deleteIfExists(Paths.get(oldFile.getFilePath()));
                recordFileRepository.delete(oldFile);
            }
        }

        // 요청에서 새로운 파일 저장
        for(MultipartFile file : newFiles) {
            boolean alreadyExists = existingFiles.stream()
                    .anyMatch(old -> old.getOriginalFileName().equals(file.getOriginalFilename()));
            if(!alreadyExists) {
                // 저장 경로
                Path savePath = Paths.get(".uploads/records/" + fileType.getDirectory(), file.getOriginalFilename());
                Files.createDirectories(savePath.getParent());
                Files.write(savePath, file.getBytes());

                // DB 저장
                RecordFile newFile = RecordFile.builder()
                        .record(record)
                        .fileType(fileType)
                        .originalFileName(file.getOriginalFilename())
                        .fileName(UUID.randomUUID() + "_" + file.getOriginalFilename())
                        .filePath(savePath.toString())
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .build();

                recordFileRepository.save(newFile);
            }
        }
    }
}
