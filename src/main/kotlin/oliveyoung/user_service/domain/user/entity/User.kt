package oliveyoung.user_service.domain.user.entity

import java.time.LocalDateTime

data class User(
    val id: Long? = null,
    val username: String,
    val email: String,
    val password: String,
    val profileImageUrl: String? = null,
    val role: Role = Role.USER,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
