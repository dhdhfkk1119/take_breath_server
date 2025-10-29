package com.take.take_breath.record;

import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.record.dto.RecordListResponse;
import com.take.take_breath.record.dto.RecordResponse;
import com.take.take_breath.record.dto.RecordSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;

    private final String commonSavedPath = "/uploads/record/images/";

    @Transactional(readOnly = true)
    public Page<RecordListResponse> getRecordList(int page, int size, String sortField, String sortType) {
        Sort.Direction direction = sortType.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return recordRepository.findAll(pageable)
                .map(RecordListResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public RecordResponse getRecord(Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 기록이 존재하지 않습니다"));

        return RecordResponse.fromEntity(record);
    }

    public Long saveRecord(Long memberId, RecordSaveRequest request) {
        // 작성자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        // Record 엔티티 생성
        Record record = Record.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .recordDate(request.getRecordDate())
                .build();

        // 파일 처리
        if(request.getImageFiles() != null) {
            for(MultipartFile file : request.getImageFiles()) {
                String savedPath = commonSavedPath + file.getFile()
            }
        }

        if(request.getAudioFiles() != null) {

        }

        if(request.getVideoFiles() != null) {

        }

    }

}
