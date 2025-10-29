package com.take.take_breath.counselor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCounselor is a Querydsl query type for Counselor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCounselor extends EntityPathBase<Counselor> {

    private static final long serialVersionUID = 768325170L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCounselor counselor = new QCounselor("counselor");

    public final StringPath gender = createString("gender");

    public final StringPath hashtags = createString("hashtags");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath introduction = createString("introduction");

    public final ListPath<CounselorLicense, QCounselorLicense> licenses = this.<CounselorLicense, QCounselorLicense>createList("licenses", CounselorLicense.class, QCounselorLicense.class, PathInits.DIRECT2);

    public final com.take.take_breath.members.QMember member;

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath profileImage = createString("profileImage");

    public final StringPath specialty = createString("specialty");

    public final EnumPath<com.take.take_breath.members.Status> status = createEnum("status", com.take.take_breath.members.Status.class);

    public QCounselor(String variable) {
        this(Counselor.class, forVariable(variable), INITS);
    }

    public QCounselor(Path<? extends Counselor> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCounselor(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCounselor(PathMetadata metadata, PathInits inits) {
        this(Counselor.class, metadata, inits);
    }

    public QCounselor(Class<? extends Counselor> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.take.take_breath.members.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

