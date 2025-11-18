package oliveyoung.community.domain.post.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 게시글 생성 요청 DTO
 */
data class CreatePostRequest(
    @field:NotNull(message = "user_id는 필수입니다")
    val userId: Long,
    @field:NotBlank(message = "title은 필수입니다")
    @field:Size(max = 200, message = "title은 200자 이내여야 합니다")
    val title: String,
    @field:NotBlank(message = "content는 필수입니다")
    @field:Size(max = 5000, message = "content는 5000자 이내여야 합니다")
    val content: String,
    @field:Size(max = 500, message = "image_url은 500자 이내여야 합니다")
    val imageUrl: String? = null,
)

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

/**
 * 좋아요 생성 요청 DTO
 */
data class CreateLikeRequest(
    @field:NotNull(message = "user_id는 필수입니다")
    val userId: Long,
)
