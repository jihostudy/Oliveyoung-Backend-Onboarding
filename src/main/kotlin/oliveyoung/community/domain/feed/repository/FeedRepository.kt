package oliveyoung.community.domain.feed.repository

import oliveyoung.community.domain.post.entity.Post

/**
 * Feed Repository 인터페이스
 *
 * 커서 기반 페이지네이션으로 피드 조회
 */
interface FeedRepository {
    /**
     * 로그인 사용자의 피드 조회 (커서 기반)
     *
     * 팔로우한 사용자들의 최신 게시글 목록
     * - 작성일시 기준 내림차순 정렬
     * - deleted_at IS NULL인 게시글만
     * - size + 1개를 조회하여 hasNext 판단
     *
     * @param userId 조회하는 사용자 ID
     * @param cursor 커서 (게시글 ID, null이면 첫 페이지)
     * @param size 조회할 게시글 수 + 1
     * @return 게시글 엔티티 목록
     */
    fun findFeedByUserId(
        userId: Long,
        cursor: Long?,
        size: Int,
    ): List<Post>

    /**
     * 비로그인 사용자의 피드 조회 (커서 기반)
     *
     * 전체 최신 게시글 목록
     * - 작성일시 기준 내림차순 정렬
     * - deleted_at IS NULL인 게시글만
     * - size + 1개를 조회하여 hasNext 판단
     *
     * @param cursor 커서 (게시글 ID, null이면 첫 페이지)
     * @param size 조회할 게시글 수 + 1
     * @return 게시글 엔티티 목록
     */
    fun findPublicFeed(
        cursor: Long?,
        size: Int,
    ): List<Post>
}
