package oliveyoung.community.domain.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import oliveyoung.community.domain.user.entity.Role

data class RegisterRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50)
    val username: String,
    @field:NotBlank(message = "Email is required")
    @field:Email
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 100)
    val password: String,
    val role: Role? = Role.USER,
    val image_url: String? = null,
)
