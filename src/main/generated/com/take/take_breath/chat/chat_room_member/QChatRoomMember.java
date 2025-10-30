package com.take.take_breath.chat.chat_room_member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoomMember is a Querydsl query type for ChatRoomMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoomMember extends EntityPathBase<ChatRoomMember> {

    private static final long serialVersionUID = 796856318L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoomMember chatRoomMember = new QChatRoomMember("chatRoomMember");

    public final com.take.take_breath.chat.chat_room.QChatRoom chatRoom;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.sql.Timestamp> joinedAt = createDateTime("joinedAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> lastReadAt = createDateTime("lastReadAt", java.sql.Timestamp.class);

    public final NumberPath<Long> lastReadMessageId = createNumber("lastReadMessageId", Long.class);

    public final com.take.take_breath.members.QMember member;

    public QChatRoomMember(String variable) {
        this(ChatRoomMember.class, forVariable(variable), INITS);
    }

    public QChatRoomMember(Path<? extends ChatRoomMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoomMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoomMember(PathMetadata metadata, PathInits inits) {
        this(ChatRoomMember.class, metadata, inits);
    }

    public QChatRoomMember(Class<? extends ChatRoomMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new com.take.take_breath.chat.chat_room.QChatRoom(forProperty("chatRoom")) : null;
        this.member = inits.isInitialized("member") ? new com.take.take_breath.members.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

