package oliveyoung.community.domain.post.dto.response

/**
 * 댓글 목록 응답 DTO
 */
data class CommentListResponse(
    val items: List<CommentResponse>,
)
