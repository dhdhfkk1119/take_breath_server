package com.take.take_breath.community.community_post;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.take.take_breath.community.community_category.QCommunityCategory.communityCategory;
import static com.take.take_breath.community.community_comment.QCommunityComment.communityComment;
import static com.take.take_breath.community.community_post.QCommunityPost.communityPost;
import static com.take.take_breath.community.community_post_image.QCommunityPostImage.communityPostImage;
import static com.take.take_breath.members.QMember.member;

@Repository
@RequiredArgsConstructor
public class CommunityPostRepositoryImpl implements CommunityPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    // 전체 조회 (category, comments 포함)
    @Override
    public Page<CommunityPost> findAllWithCategoryAndComments(Pageable pageable) {
        List<CommunityPost> posts = queryFactory
                .selectFrom(communityPost)
                .distinct()
                .leftJoin(communityPost.category, communityCategory).fetchJoin()
                .leftJoin(communityPost.member, member).fetchJoin()
                .where(communityPost.deletedAt.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable))
                .fetch();

        Long total = queryFactory
                .select(communityPost.count())
                .from(communityPost)
                .where(communityPost.deletedAt.isNull())
                .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    // 댓글 개수 조회
    @Override
    public Map<Long, Long> getCommentCountsByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Tuple> results = queryFactory
                .select(communityComment.post.id, communityComment.count())
                .from(communityComment)
                .where(
                        communityComment.post.id.in(postIds),
                        communityComment.deletedAt.isNull()
                )
                .groupBy(communityComment.post.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(communityComment.post.id),
                        tuple -> tuple.get(communityComment.count())
                ));
    }

    // 상세 조회 (comments 포함)
    @Override
    public Optional<CommunityPost> findByIdWithComments(Long postId) {
        CommunityPost post = queryFactory
                .selectFrom(communityPost)
                .leftJoin(communityPost.category, communityCategory).fetchJoin()
                .leftJoin(communityPost.member, member).fetchJoin()
                .leftJoin(communityPost.images, communityPostImage).fetchJoin()
                .leftJoin(communityPost.comments, communityComment).fetchJoin()
                .where(
                        communityPost.id.eq(postId),
                        communityPost.deletedAt.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(post);
    }

    @Override
    public Optional<CommunityPost> findByIdWithCategory(Long postId) {
        CommunityPost post = queryFactory
                .selectFrom(communityPost)
                .leftJoin(communityPost.category, communityCategory).fetchJoin()
                .leftJoin(communityPost.member, member).fetchJoin()
                .where(
                        communityPost.id.eq(postId),
                        communityPost.deletedAt.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(post);
    }

    // 검색 (키워드, 카테고리)
    @Override
    public Page<CommunityPost> findBySearchOption(CommunityPostRequest.SearchDTO searchDTO, Pageable pageable) {
        List<CommunityPost> posts = queryFactory
                .selectFrom(communityPost)
                .distinct()
                .leftJoin(communityPost.category, communityCategory).fetchJoin()
                .leftJoin(communityPost.member, member).fetchJoin()
                .where(
                        communityPost.deletedAt.isNull(),
                        keywordContains(searchDTO.getKeyword()),
                        categoryIdsIn(searchDTO.getCategoryIds())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable))
                .fetch();

        Long total = queryFactory
                .select(communityPost.count())
                .from(communityPost)
                .where(
                        communityPost.deletedAt.isNull(),
                        keywordContains(searchDTO.getKeyword()),
                        categoryIdsIn(searchDTO.getCategoryIds())
                )
                .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    // 삭제되지 않은 글만 조회
    @Override
    public List<CommunityPost> findAllActive() {
        return queryFactory
                .selectFrom(communityPost)
                .where(communityPost.deletedAt.isNull())
                .fetch();
    }

    // memberId로 게시글 목록 조회
    @Override
    public Page<CommunityPost> findByMemberId(Long memberId, Pageable pageable) {
        List<CommunityPost> posts = queryFactory
                .selectFrom(communityPost)
                .leftJoin(communityPost.category, communityCategory).fetchJoin()
                .leftJoin(communityPost.member, member).fetchJoin()
                .where(
                        communityPost.member.id.eq(memberId),
                        communityPost.deletedAt.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable))
                .fetch();

        Long total = queryFactory
                .select(communityPost.count())
                .from(communityPost)
                .where(
                        communityPost.member.id.eq(memberId),
                        communityPost.deletedAt.isNull()
                )
                .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    // 소프트 삭제
    @Override
    public int softDelete(Long id) {
        long count = queryFactory
                .update(communityPost)
                .set(communityPost.deletedAt, LocalDateTime.now())
                .where(communityPost.id.eq(id))
                .execute();

        return (int) count;
    }

    // 키워드 검색 (제목, 내용)
    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return communityPost.title.contains(keyword)
                .or(communityPost.content.contains(keyword));
    }

    // 카테고리 검색
    private BooleanExpression categoryIdsIn(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return communityPost.category.id.in(categoryIds);
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{communityPost.createdAt.desc()};
        }

        return pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty();
                    boolean isAsc = order.isAscending();

                    return switch (property) {
                        case "createdAt" -> isAsc ? communityPost.createdAt.asc() : communityPost.createdAt.desc();
                        case "likeCount" -> isAsc ? communityPost.likeCount.asc() : communityPost.likeCount.desc();
                        case "viewCount" -> isAsc ? communityPost.viewCount.asc() : communityPost.viewCount.desc();
                        case "id" -> isAsc ? communityPost.id.asc() : communityPost.id.desc();
                        default -> communityPost.createdAt.desc();
                    };
                })
                .toArray(size -> new OrderSpecifier<?>[size]);
    }
}