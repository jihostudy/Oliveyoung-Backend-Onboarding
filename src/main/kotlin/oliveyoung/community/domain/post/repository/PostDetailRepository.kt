package oliveyoung.community.domain.post.repository

import oliveyoung.community.domain.post.entity.Comment
import oliveyoung.community.domain.post.entity.Like

/**
 * PostDetail Repository 인터페이스
 *
 * 게시글 상세 정보 관리 (댓글 + 좋아요)
 */
interface PostDetailRepository {
    // ========== Comment 관련 ==========

    /**
     * 댓글 생성
     */
    fun insertComment(comment: Comment): Comment

    /**
     * 게시글의 댓글 목록 조회
     */
    fun findCommentsByPostIdOrderByCreatedAtDesc(postId: Long): List<Comment>

    /**
     * 게시글의 댓글 수 조회
     */
    fun countCommentsByPostId(postId: Long): Int

    /**
     * 여러 게시글의 댓글 수 배치 조회
     * @return Map<PostId, CommentCount>
     */
    fun countCommentsByPostIds(postIds: List<Long>): Map<Long, Int>

    // ========== Like 관련 ==========

    /**
     * 좋아요 생성
     */
    fun insertLike(like: Like): Like

    /**
     * 좋아요 삭제
     */
    fun deleteLikeByPostIdAndUserId(
        postId: Long,
        userId: Long,
    )

    /**
     * 좋아요 여부 확인
     */
    fun existsLikeByPostIdAndUserId(
        postId: Long,
        userId: Long,
    ): Boolean

    /**
     * 게시글의 좋아요 수 조회
     */
    fun countLikesByPostId(postId: Long): Int

    /**
     * 여러 게시글의 좋아요 수 배치 조회
     * @return Map<PostId, LikeCount>
     */
    fun countLikesByPostIds(postIds: List<Long>): Map<Long, Int>

    /**
     * 사용자가 좋아요한 게시글 ID 목록 조회
     */
    fun findLikedPostIdsByUserId(
        userId: Long,
        postIds: List<Long>,
    ): List<Long>
}
