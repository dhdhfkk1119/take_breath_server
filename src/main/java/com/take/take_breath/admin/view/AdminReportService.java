package com.take.take_breath.admin.view;

import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminReportService {

    private final CommunityReportRepository communityReportRepository;
    private final CommentReportRepository commentReportRepository;

    /**
     * 전체 신고 목록 조회 (게시글 + 댓글 통합)
     */
    public List<Object> getAllReports(Pageable pageable) {
        List<Object> allReports = new ArrayList<>();

        // 게시글 신고
        Page<CommunityReport> postReports = communityReportRepository.findAll(pageable);
        allReports.addAll(postReports.getContent());

        // 댓글 신고
        Page<CommentReport> commentReports = commentReportRepository.findAll(pageable);
        allReports.addAll(commentReports.getContent());

        return allReports;
    }

    /**
     * 상태별 게시글 신고 조회
     */
    public List<CommunityReport> getCommunityReportsByStatus(CommunityReportStatus status, Pageable pageable) {
        // Repository에 메서드 추가 필요
        return communityReportRepository.findAll(pageable).getContent().stream()
                .filter(report -> report.getStatus() == status)
                .toList();
    }

    /**
     * 상태별 댓글 신고 조회
     */
    public List<CommentReport> getCommentReportsByStatus(CommunityReportStatus status, Pageable pageable) {
        return commentReportRepository.findAll(pageable).getContent().stream()
                .filter(report -> report.getStatus() == status)
                .toList();
    }

    /**
     * 게시글 신고 전체 조회
     */
    public List<CommunityReport> getAllCommunityReports(Pageable pageable) {
        return communityReportRepository.findAll(pageable).getContent();
    }

    /**
     * 댓글 신고 전체 조회
     */
    public List<CommentReport> getAllCommentReports(Pageable pageable) {
        return commentReportRepository.findAll(pageable).getContent();
    }
}