package com.take.take_breath.community.community_post_image;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunityPostImage is a Querydsl query type for CommunityPostImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityPostImage extends EntityPathBase<CommunityPostImage> {

    private static final long serialVersionUID = -1288121031L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunityPostImage communityPostImage = new QCommunityPostImage("communityPostImage");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final com.take.take_breath.community.community_post.QCommunityPost post;

    public QCommunityPostImage(String variable) {
        this(CommunityPostImage.class, forVariable(variable), INITS);
    }

    public QCommunityPostImage(Path<? extends CommunityPostImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunityPostImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunityPostImage(PathMetadata metadata, PathInits inits) {
        this(CommunityPostImage.class, metadata, inits);
    }

    public QCommunityPostImage(Class<? extends CommunityPostImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.take.take_breath.community.community_post.QCommunityPost(forProperty("post"), inits.get("post")) : null;
    }

}

