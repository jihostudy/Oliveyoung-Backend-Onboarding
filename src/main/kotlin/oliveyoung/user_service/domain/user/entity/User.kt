package oliveyoung.user_service.domain.user.entity

import java.time.LocalDateTime


data class User(
    val id: Long? = null,
    val username: String,
    val email: String,
    val password: String,
    val imageUrl: String? = null,
    val role: Role = Role.USER,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null  // Soft Delete
)
