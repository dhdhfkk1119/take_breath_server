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
     * @param memberId
     * @param request
     * @return
     */
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
