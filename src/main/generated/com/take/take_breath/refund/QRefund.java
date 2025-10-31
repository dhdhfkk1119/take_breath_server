package com.take.take_breath.refund;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRefund is a Querydsl query type for Refund
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefund extends EntityPathBase<Refund> {

    private static final long serialVersionUID = -416028194L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRefund refund = new QRefund("refund");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath impCancelUid = createString("impCancelUid");

    public final com.take.take_breath.members.QMember member;

    public final com.take.take_breath.payment.QPayment payment;

    public final DateTimePath<java.sql.Timestamp> processedAt = createDateTime("processedAt", java.sql.Timestamp.class);

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> refundAmount = createNumber("refundAmount", Long.class);

    public final EnumPath<RefundStatus> status = createEnum("status", RefundStatus.class);

    public QRefund(String variable) {
        this(Refund.class, forVariable(variable), INITS);
    }

    public QRefund(Path<? extends Refund> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRefund(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRefund(PathMetadata metadata, PathInits inits) {
        this(Refund.class, metadata, inits);
    }

    public QRefund(Class<? extends Refund> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.take.take_breath.members.QMember(forProperty("member"), inits.get("member")) : null;
        this.payment = inits.isInitialized("payment") ? new com.take.take_breath.payment.QPayment(forProperty("payment"), inits.get("payment")) : null;
    }

}

