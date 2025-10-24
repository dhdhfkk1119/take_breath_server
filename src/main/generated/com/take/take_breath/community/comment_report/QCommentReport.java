package com.take.take_breath.community.comment_report;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentReport is a Querydsl query type for CommentReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentReport extends EntityPathBase<CommentReport> {

    private static final long serialVersionUID = 690341326L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentReport commentReport = new QCommentReport("commentReport");

    public final ListPath<com.take.take_breath.community.comment_report_process.CommentReportProcess, com.take.take_breath.community.comment_report_process.QCommentReportProcess> adminComments = this.<com.take.take_breath.community.comment_report_process.CommentReportProcess, com.take.take_breath.community.comment_report_process.QCommentReportProcess>createList("adminComments", com.take.take_breath.community.comment_report_process.CommentReportProcess.class, com.take.take_breath.community.comment_report_process.QCommentReportProcess.class, PathInits.DIRECT2);

    public final com.take.take_breath.community.community_comment.QCommunityComment comment;

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> reporterId = createNumber("reporterId", Long.class);

    public final EnumPath<com.take.take_breath.community.community_report.CommunityReportStatus> status = createEnum("status", com.take.take_breath.community.community_report.CommunityReportStatus.class);

    public QCommentReport(String variable) {
        this(CommentReport.class, forVariable(variable), INITS);
    }

    public QCommentReport(Path<? extends CommentReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentReport(PathMetadata metadata, PathInits inits) {
        this(CommentReport.class, metadata, inits);
    }

    public QCommentReport(Class<? extends CommentReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new com.take.take_breath.community.community_comment.QCommunityComment(forProperty("comment"), inits.get("comment")) : null;
    }

}

