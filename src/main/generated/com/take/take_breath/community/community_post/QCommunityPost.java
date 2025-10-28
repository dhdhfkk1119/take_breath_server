package com.take.take_breath.community.community_post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunityPost is a Querydsl query type for CommunityPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityPost extends EntityPathBase<CommunityPost> {

    private static final long serialVersionUID = -931219930L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunityPost communityPost = new QCommunityPost("communityPost");

    public final com.take.take_breath.community.community_category.QCommunityCategory category;

    public final ListPath<com.take.take_breath.community.community_comment.CommunityComment, com.take.take_breath.community.community_comment.QCommunityComment> comments = this.<com.take.take_breath.community.community_comment.CommunityComment, com.take.take_breath.community.community_comment.QCommunityComment>createList("comments", com.take.take_breath.community.community_comment.CommunityComment.class, com.take.take_breath.community.community_comment.QCommunityComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.take.take_breath.community.community_post_image.CommunityPostImage, com.take.take_breath.community.community_post_image.QCommunityPostImage> images = this.<com.take.take_breath.community.community_post_image.CommunityPostImage, com.take.take_breath.community.community_post_image.QCommunityPostImage>createList("images", com.take.take_breath.community.community_post_image.CommunityPostImage.class, com.take.take_breath.community.community_post_image.QCommunityPostImage.class, PathInits.DIRECT2);

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final ListPath<com.take.take_breath.community.community_post_like.CommunityPostLike, com.take.take_breath.community.community_post_like.QCommunityPostLike> likes = this.<com.take.take_breath.community.community_post_like.CommunityPostLike, com.take.take_breath.community.community_post_like.QCommunityPostLike>createList("likes", com.take.take_breath.community.community_post_like.CommunityPostLike.class, com.take.take_breath.community.community_post_like.QCommunityPostLike.class, PathInits.DIRECT2);

    public final com.take.take_breath.members.QMember member;

    public final NumberPath<Integer> reportCount = createNumber("reportCount", Integer.class);

    public final StringPath thumbnailImageUrl = createString("thumbnailImageUrl");

    public final StringPath title = createString("title");

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QCommunityPost(String variable) {
        this(CommunityPost.class, forVariable(variable), INITS);
    }

    public QCommunityPost(Path<? extends CommunityPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunityPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunityPost(PathMetadata metadata, PathInits inits) {
        this(CommunityPost.class, metadata, inits);
    }

    public QCommunityPost(Class<? extends CommunityPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.take.take_breath.community.community_category.QCommunityCategory(forProperty("category")) : null;
        this.member = inits.isInitialized("member") ? new com.take.take_breath.members.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

