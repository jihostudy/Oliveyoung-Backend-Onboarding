package oliveyoung.user_service.domain.user.entity

import java.time.LocalDateTime

data class Follow(
    val id: Long? = null,
    val followerId: Long,
    val followingId: Long,
    val createdAt: LocalDateTime
)