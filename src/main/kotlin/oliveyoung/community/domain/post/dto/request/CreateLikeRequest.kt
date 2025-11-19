package oliveyoung.community.domain.post.dto.request

import jakarta.validation.constraints.NotNull

/**
 * 좋아요 생성 요청 DTO
 */
data class CreateLikeRequest(
    @field:NotNull(message = "user_id는 필수입니다")
    val userId: Long,
)
