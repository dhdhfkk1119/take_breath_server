package com.take.take_breath.record;

import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath._core._exception.Exception500;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final RecordFileRepository recordFileRepository;
    private final MemberRepository memberRepository;
    private final RecordPdfService recordPdfService;
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
     * 특정 조건을 통한 목록 조회(페이징 + 동적 쿼리)
     * @param email
     * @param condition
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<RecordListResponse> searchWithCondition(
            String email, RecordSearchCondition condition, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recordRepository.searchWithCondition(email, condition, pageable)
                .map(RecordListResponse::fromEntity);
    }

    /**
     * 특정 기록 상세 조회
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Record getRecord(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 기록이 존재하지 않습니다"));
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

        // 파일 저장 처리
        try {
            processFiles(savedRecord, request.getImageFiles(), FileType.IMAGE);
            processFiles(savedRecord, request.getAudioFiles(), FileType.AUDIO);
            processFiles(savedRecord, request.getVideoFiles(), FileType.VIDEO);
        } catch (IOException e) {
            throw new Exception500("기록 파일 저장에 실패했습니다.");
        }

        return savedRecord;
    }

    /**
     * 기록 수정 - 수정된 파일 처리 방법 필요
     * @param email
     * @param recordId
     * @param request
     * @return
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

        try {
            // 기존 파일 전체 삭제
            deleteAllRecordFiles(record);

            // 기본 필드 업데이트
            record.setTitle(request.getTitle());
            record.setContent(request.getContent());

            // 새로운 파일 저장
            processFiles(record, request.getImageFiles(), FileType.IMAGE);
            processFiles(record, request.getAudioFiles(), FileType.AUDIO);
            processFiles(record, request.getVideoFiles(), FileType.VIDEO);
        } catch (IOException e) {
            throw new Exception500("파일 수정 중 에러 발생");
        }
        
        return recordRepository.save(record);
    }

    /**
     * 기록 삭제 - 로컬 경로에 저장된 데이터 삭제도 필요함
     * @param recordId
     * @return
     */
    public void deleteRecord(String email, Long recordId) {
        // 예외 처리 & 권한 처리
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new Exception404("현재 존재하지 않는 기록입니다"));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("현재 존재하지 않는 사용자입니다"));

        if (!record.getMember().getId().equals(member.getId())) {
            throw new Exception403("현재 사용자가 삭제할 권한을 가지고 있지 않습니다.");
        }

        // 파일 삭제
        deleteAllRecordFiles(record);

        // DB 삭제
        recordRepository.delete(record);
    }

    public byte[] generatePdf(String email, Long recordId) {
        try {
            // 예외 처리 & 권한 체크
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));
            Record record = recordRepository.findById(recordId)
                    .orElseThrow(() -> new Exception404("해당 기록이 존재하지 않습니다"));
            if (!record.getMember().getId().equals(member.getId())) {
                throw new Exception403("해당 기록을 수정할 권한이 없습니다");
            }

            // 2. 첨부 파일 조회
            List<RecordFile> recordFiles = recordFileRepository.findByRecordId(recordId);

            // 3. PDF 생성
            return recordPdfService.generatePdf(record, recordFiles);
        } catch (Exception e) {
            throw new Exception500("pdf 파일 생성에 실패했습니다");
        }

        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Hello from Spring + iText!"));
        document.add(new Paragraph("PDF가 정상적으로 생성되었습니다."));
        document.close();

        byte[] pdfBytes = baos.toByteArray();
        return pdfBytes;
        */
    }



    /**
     * 파일 처리 및 저장
     * @param record
     * @param files
     * @param fileType
     * @throws IOException
     */
    private void processFiles(Record record, List<MultipartFile> files, FileType fileType) throws IOException {
        if (files == null || files.isEmpty()) return;

        // 파일 타입에 따라 업로드 메서드 선택
        List<UploadedFileInfo> uploadedFiles = switch (fileType) {
            case IMAGE -> uploadFile.uploadRecordImages(files);
            case AUDIO -> uploadFile.uploadRecordAudios(files);
            case VIDEO -> uploadFile.uploadRecordVideos(files);
        };

        for (UploadedFileInfo fileInfo : uploadedFiles) {
            RecordFile recordFile = RecordFile.builder()
                    .record(record)
                    .fileType(fileType)
                    .fileName(fileInfo.getSavedFileName())
                    .originalFileName(fileInfo.getOriginalFileName())
                    .filePath(fileInfo.getFilePath())
                    .fileSize(fileInfo.getFileSize())
                    .contentType(fileInfo.getContentType())
                    .build();

            recordFileRepository.save(recordFile);
            record.addRecordFile(recordFile);
        }
    }

    /**
     * Record에 연결된 모든 파일 삭제
     * @param record
     */
    private void deleteAllRecordFiles(Record record) {
        List<RecordFile> files = record.getRecordFiles();

        if (files == null || files.isEmpty()) {
            return;
        }

        List<String> filePaths = files.stream()
                .map(RecordFile::getFilePath)
                .collect(Collectors.toList());
        uploadFile.deleteFiles(filePaths);

        for (RecordFile file : files) {
            recordFileRepository.delete(file);
        }

        files.clear();
    }

}
