package oliveyoung.community.domain.post.dto.response

/**
 * 댓글 반응 정보
 */
data class CommentReaction(
    val count: Int,
    val items: List<CommentResponse>,
)
