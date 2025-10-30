package com.take.take_breath.counselor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCounselorApproval is a Querydsl query type for CounselorApproval
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCounselorApproval extends EntityPathBase<CounselorApproval> {

    private static final long serialVersionUID = 429076117L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCounselorApproval counselorApproval = new QCounselorApproval("counselorApproval");

    public final QCounselor counselor;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath reason = createString("reason");

    public final EnumPath<com.take.take_breath.members.Status> status = createEnum("status", com.take.take_breath.members.Status.class);

    public QCounselorApproval(String variable) {
        this(CounselorApproval.class, forVariable(variable), INITS);
    }

    public QCounselorApproval(Path<? extends CounselorApproval> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCounselorApproval(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCounselorApproval(PathMetadata metadata, PathInits inits) {
        this(CounselorApproval.class, metadata, inits);
    }

    public QCounselorApproval(Class<? extends CounselorApproval> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.counselor = inits.isInitialized("counselor") ? new QCounselor(forProperty("counselor"), inits.get("counselor")) : null;
    }

}

