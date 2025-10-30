package com.take.take_breath.record;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecordFile is a Querydsl query type for RecordFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecordFile extends EntityPathBase<RecordFile> {

    private static final long serialVersionUID = -394080678L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecordFile recordFile = new QRecordFile("recordFile");

    public final StringPath contentType = createString("contentType");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath fileName = createString("fileName");

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final EnumPath<FileType> fileType = createEnum("fileType", FileType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originalFileName = createString("originalFileName");

    public final QRecord record;

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public QRecordFile(String variable) {
        this(RecordFile.class, forVariable(variable), INITS);
    }

    public QRecordFile(Path<? extends RecordFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecordFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecordFile(PathMetadata metadata, PathInits inits) {
        this(RecordFile.class, metadata, inits);
    }

    public QRecordFile(Class<? extends RecordFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.record = inits.isInitialized("record") ? new QRecord(forProperty("record"), inits.get("record")) : null;
    }

}

