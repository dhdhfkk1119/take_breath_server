package com.take.take_breath.chat.chat_message;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatMessage is a Querydsl query type for ChatMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMessage extends EntityPathBase<ChatMessage> {

    private static final long serialVersionUID = 943449991L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatMessage chatMessage = new QChatMessage("chatMessage");

    public final StringPath attachmentPath = createString("attachmentPath");

    public final com.take.take_breath.chat.chat_room.QChatRoom chatRoom;

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originalFilename = createString("originalFilename");

    public final com.take.take_breath.members.QMember sender;

    public final EnumPath<MessageStatus> status = createEnum("status", MessageStatus.class);

    public final EnumPath<MessageType> type = createEnum("type", MessageType.class);

    public QChatMessage(String variable) {
        this(ChatMessage.class, forVariable(variable), INITS);
    }

    public QChatMessage(Path<? extends ChatMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatMessage(PathMetadata metadata, PathInits inits) {
        this(ChatMessage.class, metadata, inits);
    }

    public QChatMessage(Class<? extends ChatMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new com.take.take_breath.chat.chat_room.QChatRoom(forProperty("chatRoom")) : null;
        this.sender = inits.isInitialized("sender") ? new com.take.take_breath.members.QMember(forProperty("sender"), inits.get("sender")) : null;
    }

}

