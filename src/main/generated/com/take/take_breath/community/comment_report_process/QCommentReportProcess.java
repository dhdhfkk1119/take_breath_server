package com.take.take_breath.community.comment_report_process;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentReportProcess is a Querydsl query type for CommentReportProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentReportProcess extends EntityPathBase<CommentReportProcess> {

    private static final long serialVersionUID = 1827462801L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentReportProcess commentReportProcess = new QCommentReportProcess("commentReportProcess");

    public final StringPath adminComment = createString("adminComment");

    public final NumberPath<Long> adminId = createNumber("adminId", Long.class);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.take.take_breath.community.comment_report.QCommentReport report;

    public final EnumPath<com.take.take_breath.community.community_report.CommunityReportStatus> status = createEnum("status", com.take.take_breath.community.community_report.CommunityReportStatus.class);

    public QCommentReportProcess(String variable) {
        this(CommentReportProcess.class, forVariable(variable), INITS);
    }

    public QCommentReportProcess(Path<? extends CommentReportProcess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentReportProcess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentReportProcess(PathMetadata metadata, PathInits inits) {
        this(CommentReportProcess.class, metadata, inits);
    }

    public QCommentReportProcess(Class<? extends CommentReportProcess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.report = inits.isInitialized("report") ? new com.take.take_breath.community.comment_report.QCommentReport(forProperty("report"), inits.get("report")) : null;
    }

}

