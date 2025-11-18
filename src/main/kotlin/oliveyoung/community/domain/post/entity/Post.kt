package oliveyoung.community.domain.post.entity

import java.time.LocalDateTime

/**
 * Post 엔티티
 *
 * 게시글 정보
 * - Soft Delete 적용 (deleted_at)
 */
data class Post(
    val id: Long? = null,
    val userId: Long,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
)
