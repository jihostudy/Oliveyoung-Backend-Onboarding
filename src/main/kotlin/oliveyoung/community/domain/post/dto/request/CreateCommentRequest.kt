package oliveyoung.community.domain.post.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 댓글 생성 요청 DTO
 */
data class CreateCommentRequest(
    @field:NotNull(message = "user_id는 필수입니다")
    val userId: Long,
    @field:NotBlank(message = "content는 필수입니다")
    @field:Size(max = 1000, message = "content는 1000자 이내여야 합니다")
    val content: String,
)
