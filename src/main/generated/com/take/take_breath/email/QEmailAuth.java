package com.take.take_breath.email;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmailAuth is a Querydsl query type for EmailAuth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailAuth extends EntityPathBase<EmailAuth> {

    private static final long serialVersionUID = 710477442L;

    public static final QEmailAuth emailAuth = new QEmailAuth("emailAuth");

    public final StringPath code = createString("code");

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expireAt = createDateTime("expireAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath verified = createBoolean("verified");

    public QEmailAuth(String variable) {
        super(EmailAuth.class, forVariable(variable));
    }

    public QEmailAuth(Path<? extends EmailAuth> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmailAuth(PathMetadata metadata) {
        super(EmailAuth.class, metadata);
    }

}

