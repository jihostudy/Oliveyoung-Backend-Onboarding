package oliveyoung.community.domain.post.dto.response

/**
 * 좋아요 반응 정보
 */
data class LikeReaction(
    val count: Int,
    val isLiked: Boolean,
)
