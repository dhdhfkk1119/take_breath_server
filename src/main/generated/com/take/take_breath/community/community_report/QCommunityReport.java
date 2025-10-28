package com.take.take_breath.community.community_report;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunityReport is a Querydsl query type for CommunityReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityReport extends EntityPathBase<CommunityReport> {

    private static final long serialVersionUID = -1883054322L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunityReport communityReport = new QCommunityReport("communityReport");

    public final ListPath<com.take.take_breath.community.community_report_process.CommunityReportProcess, com.take.take_breath.community.community_report_process.QCommunityReportProcess> adminComments = this.<com.take.take_breath.community.community_report_process.CommunityReportProcess, com.take.take_breath.community.community_report_process.QCommunityReportProcess>createList("adminComments", com.take.take_breath.community.community_report_process.CommunityReportProcess.class, com.take.take_breath.community.community_report_process.QCommunityReportProcess.class, PathInits.DIRECT2);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.take.take_breath.community.community_post.QCommunityPost post;

    public final StringPath reason = createString("reason");

    public final com.take.take_breath.members.QMember reporter;

    public final EnumPath<CommunityReportStatus> status = createEnum("status", CommunityReportStatus.class);

    public QCommunityReport(String variable) {
        this(CommunityReport.class, forVariable(variable), INITS);
    }

    public QCommunityReport(Path<? extends CommunityReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunityReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunityReport(PathMetadata metadata, PathInits inits) {
        this(CommunityReport.class, metadata, inits);
    }

    public QCommunityReport(Class<? extends CommunityReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.take.take_breath.community.community_post.QCommunityPost(forProperty("post"), inits.get("post")) : null;
        this.reporter = inits.isInitialized("reporter") ? new com.take.take_breath.members.QMember(forProperty("reporter"), inits.get("reporter")) : null;
    }

}

