package com.take.take_breath.community.community_report_process;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunityReportProcess is a Querydsl query type for CommunityReportProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityReportProcess extends EntityPathBase<CommunityReportProcess> {

    private static final long serialVersionUID = 1234354001L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunityReportProcess communityReportProcess = new QCommunityReportProcess("communityReportProcess");

    public final StringPath adminComment = createString("adminComment");

    public final NumberPath<Long> adminId = createNumber("adminId", Long.class);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.take.take_breath.community.community_report.QCommunityReport report;

    public final EnumPath<com.take.take_breath.community.community_report.CommunityReportStatus> status = createEnum("status", com.take.take_breath.community.community_report.CommunityReportStatus.class);

    public QCommunityReportProcess(String variable) {
        this(CommunityReportProcess.class, forVariable(variable), INITS);
    }

    public QCommunityReportProcess(Path<? extends CommunityReportProcess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunityReportProcess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunityReportProcess(PathMetadata metadata, PathInits inits) {
        this(CommunityReportProcess.class, metadata, inits);
    }

    public QCommunityReportProcess(Class<? extends CommunityReportProcess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.report = inits.isInitialized("report") ? new com.take.take_breath.community.community_report.QCommunityReport(forProperty("report"), inits.get("report")) : null;
    }

}

