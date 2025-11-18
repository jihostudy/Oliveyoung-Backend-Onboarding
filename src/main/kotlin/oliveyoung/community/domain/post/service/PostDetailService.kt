package oliveyoung.community.domain.post.service

import oliveyoung.community.domain.post.dto.request.CreateCommentRequest
import oliveyoung.community.domain.post.dto.response.CommentResponse

/**
 * PostDetail Service 인터페이스
 *
 * 게시글 상세 정보 관리 (댓글 + 좋아요)
 */
interface PostDetailService {
    // ========== Comment 관련 ==========

    /**
     * 댓글 생성
     */
    fun createComment(
        postId: Long,
        request: CreateCommentRequest,
    ): CommentResponse

    /**
     * 댓글 목록 조회
     */
    fun getComments(postId: Long): List<CommentResponse>

    // ========== Like 관련 ==========

    /**
     * 좋아요 생성
     */
    fun createLike(
        postId: Long,
        userId: Long,
    )

    /**
     * 좋아요 삭제
     */
    fun deleteLike(
        postId: Long,
        userId: Long,
    )
}
