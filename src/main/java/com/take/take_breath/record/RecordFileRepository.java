package com.take.take_breath.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordFileRepository extends JpaRepository<RecordFile, Long> {
    // 기본 CRUD
    List<RecordFile> findByRecordId(Long recordId);
    List<RecordFile> findByRecordIdAndFileType(Long recordId, FileType fileType);
}
