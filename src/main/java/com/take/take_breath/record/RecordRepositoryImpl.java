package com.take.take_breath.record;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.take.take_breath.record.dto.RecordSearchCondition;
import com.take.take_breath.record.dto.RecordSearchCondition.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.sql.Timestamp;
import java.util.List;

import static com.take.take_breath.record.QRecord.record;
import static com.take.take_breath.record.QRecordFile.recordFile;


@RequiredArgsConstructor
public class RecordRepositoryImpl implements RecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 통합 검색
     * @param email
     * @param condition
     * @param pageable
     * @return
     */
    @Override
    public Page<Record> searchWithCondition(String email, RecordSearchCondition condition, Pageable pageable) {
        // searchType이 null이 기본값을 TITLE을 지정
        SearchType searchType = condition.getSearchTarget() != null
                ? condition.getSearchTarget()
                : SearchType.TITLE;

        List<Record> content = queryFactory
                .selectFrom(record)
                .where(
                        memberEmailEq(email),
                        keywordSearch(condition.getKeyword(), searchType),  // keyword + target
                        recordDateBetween(condition.getStartDate(), condition.getEndDate()),
                        hasFileType(FileType.IMAGE, condition.getHasImage()),
                        hasFileType(FileType.AUDIO, condition.getHasAudio()),
                        hasFileType(FileType.VIDEO, condition.getHasVideo())
                )
                .orderBy(record.recordDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(record.count())
                .from(record)
                .where(
                        memberEmailEq(email),
                        keywordSearch(condition.getKeyword(), searchType),
                        recordDateBetween(condition.getStartDate(), condition.getEndDate()),
                        hasFileType(FileType.IMAGE, condition.getHasImage()),
                        hasFileType(FileType.AUDIO, condition.getHasAudio()),
                        hasFileType(FileType.VIDEO, condition.getHasVideo())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // ==================================================
    // 추가 메서드

    /**
     * 사용자 이메일 조건
     * @param email
     * @return
     */
    private BooleanExpression memberEmailEq(String email) {
        return email != null ? record.member.email.eq(email) : null;
    }

    /**
     * 키워드 검색 조건
     * @param keyword
     * @param type
     * @return
     */
    private BooleanExpression keywordSearch(String keyword, SearchType type) {
        if(keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        return switch (type) {
            case TITLE ->
                record.title.contains(keyword);
            case CONTENT ->
                record.content.contains(keyword);
            case ALL ->
                record.title.contains(keyword).or(record.content.contains(keyword));
        };
    }

    /**
     * 날짜 범위 검색 조건
     * @param startDate
     * @param endDate
     * @return
     */
    private BooleanExpression recordDateBetween(Timestamp startDate, Timestamp endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        BooleanExpression result = null;

        if (startDate != null) {
            result = record.recordDate.goe(startDate);  // >=
        }

        if (endDate != null) {
            if (result != null) {
                result = result.and(record.recordDate.loe(endDate));  // <=
            } else {
                result = record.recordDate.loe(endDate);
            }
        }

        return result;
    }

    /**
     * 특정 파일 타입이 있는 기록만 조회
     * @param fileType
     * @param hasFile
     * @return
     */
    private BooleanExpression hasFileType(FileType fileType, Boolean hasFile) {
        if (hasFile == null) {
            return null;  // 조건 무시
        }

        if (hasFile) {
            // hasFile = true: 해당 파일이 있는 기록만
            return JPAExpressions
                    .selectOne()
                    .from(recordFile)
                    .where(
                            recordFile.record.eq(record),
                            recordFile.fileType.eq(fileType)
                    )
                    .exists();
        } else {
            // hasFile = false: 해당 파일이 없는 기록만
            return JPAExpressions
                    .selectOne()
                    .from(recordFile)
                    .where(
                            recordFile.record.eq(record),
                            recordFile.fileType.eq(fileType)
                    )
                    .notExists();  // ✅ NOT EXISTS 사용!
        }


        /*
        if (hasFile == null || !hasFile) {
            return null;
        }

        return JPAExpressions
                .selectOne()
                .from(recordFile)
                .where(
                        recordFile.record.eq(record),
                        recordFile.fileType.eq(fileType)
                )
                .exists();
         */
    }
}
