package oliveyoung.community.domain.user.entity

import java.time.LocalDateTime

data class User(
    val id: Long? = null,
    val username: String,
    val email: String,
    val password: String,
    val imageUrl: String? = null,
    val role: Role = Role.USER,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null, // Soft Delete
)
