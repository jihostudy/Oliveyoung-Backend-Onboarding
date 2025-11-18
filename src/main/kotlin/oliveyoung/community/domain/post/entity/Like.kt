package oliveyoung.community.domain.post.entity

import java.time.LocalDateTime

/**
 * Like 엔티티
 *
 * 게시글 좋아요
 * - Soft Delete 없음 (생성/삭제만)
 */
data class Like(
    val id: Long? = null,
    val postId: Long,
    val userId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
