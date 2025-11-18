package oliveyoung.community.domain.post.entity

import java.time.LocalDateTime

/**
 * Comment 엔티티
 *
 * 게시글 댓글
 * - Soft Delete 적용 (deleted_at)
 */
data class Comment(
    val id: Long? = null,
    val postId: Long,
    val userId: Long,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
)
