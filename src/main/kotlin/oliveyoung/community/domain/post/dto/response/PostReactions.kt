package oliveyoung.community.domain.post.dto.response

/**
 * 게시글 반응 정보
 */
data class PostReactions(
    val like: LikeReaction,
    val comment: CommentReaction,
)
