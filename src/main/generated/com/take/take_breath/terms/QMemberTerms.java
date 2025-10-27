package com.take.take_breath.terms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberTerms is a Querydsl query type for MemberTerms
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberTerms extends EntityPathBase<MemberTerms> {

    private static final long serialVersionUID = -1433002986L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberTerms memberTerms = new QMemberTerms("memberTerms");

    public final BooleanPath agreed = createBoolean("agreed");

    public final DateTimePath<java.time.LocalDateTime> agreedAt = createDateTime("agreedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.take.take_breath.members.QMember member;

    public final QTerms terms;

    public QMemberTerms(String variable) {
        this(MemberTerms.class, forVariable(variable), INITS);
    }

    public QMemberTerms(Path<? extends MemberTerms> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberTerms(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberTerms(PathMetadata metadata, PathInits inits) {
        this(MemberTerms.class, metadata, inits);
    }

    public QMemberTerms(Class<? extends MemberTerms> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.take.take_breath.members.QMember(forProperty("member"), inits.get("member")) : null;
        this.terms = inits.isInitialized("terms") ? new QTerms(forProperty("terms")) : null;
    }

}

