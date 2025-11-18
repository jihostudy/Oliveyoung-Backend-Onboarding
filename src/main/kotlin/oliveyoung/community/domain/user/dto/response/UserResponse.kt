package oliveyoung.community.domain.user.dto.response

import oliveyoung.community.domain.user.entity.User

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val image_url: String?,
    val role: String,
    val created_at: String,
    val updated_at: String,
) {
    companion object {
        fun from(user: User): UserResponse =
            UserResponse(
                id = user.id!!,
                name = user.username,
                email = user.email,
                image_url = user.imageUrl,
                role = user.role.name.lowercase(),
                created_at = user.createdAt.toString(),
                updated_at = user.updatedAt.toString(),
            )
    }
}
