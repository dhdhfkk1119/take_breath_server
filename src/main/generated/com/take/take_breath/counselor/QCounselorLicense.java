package com.take.take_breath.counselor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCounselorLicense is a Querydsl query type for CounselorLicense
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCounselorLicense extends EntityPathBase<CounselorLicense> {

    private static final long serialVersionUID = -1797297745L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCounselorLicense counselorLicense = new QCounselorLicense("counselorLicense");

    public final QCounselor counselor;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath licenseImage = createString("licenseImage");

    public final StringPath licenseName = createString("licenseName");

    public final StringPath licenseNumber = createString("licenseNumber");

    public final StringPath licenseRegiNumber = createString("licenseRegiNumber");

    public QCounselorLicense(String variable) {
        this(CounselorLicense.class, forVariable(variable), INITS);
    }

    public QCounselorLicense(Path<? extends CounselorLicense> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCounselorLicense(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCounselorLicense(PathMetadata metadata, PathInits inits) {
        this(CounselorLicense.class, metadata, inits);
    }

    public QCounselorLicense(Class<? extends CounselorLicense> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.counselor = inits.isInitialized("counselor") ? new QCounselor(forProperty("counselor"), inits.get("counselor")) : null;
    }

}

