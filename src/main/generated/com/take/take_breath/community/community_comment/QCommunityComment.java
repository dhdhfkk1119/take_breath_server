package com.take.take_breath.community.community_comment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunityComment is a Querydsl query type for CommunityComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityComment extends EntityPathBase<CommunityComment> {

    private static final long serialVersionUID = -1415364432L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunityComment communityComment = new QCommunityComment("communityComment");

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.take.take_breath.members.QMember member;

    public final com.take.take_breath.community.community_post.QCommunityPost post;

    public final NumberPath<Integer> reportCount = createNumber("reportCount", Integer.class);

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public QCommunityComment(String variable) {
        this(CommunityComment.class, forVariable(variable), INITS);
    }

    public QCommunityComment(Path<? extends CommunityComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunityComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunityComment(PathMetadata metadata, PathInits inits) {
        this(CommunityComment.class, metadata, inits);
    }

    public QCommunityComment(Class<? extends CommunityComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.take.take_breath.members.QMember(forProperty("member"), inits.get("member")) : null;
        this.post = inits.isInitialized("post") ? new com.take.take_breath.community.community_post.QCommunityPost(forProperty("post"), inits.get("post")) : null;
    }

}

