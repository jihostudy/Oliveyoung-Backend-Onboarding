package oliveyoung.community.domain.post.dto.response

import oliveyoung.community.common.util.TimeUtils
import oliveyoung.community.domain.post.entity.Post

/**
 * 게시글 응답 DTO
 */
data class PostResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val reactions: PostReactions,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun from(
            post: Post,
            likeCount: Int = 0,
            isLiked: Boolean = false,
            commentCount: Int = 0,
            comments: List<CommentResponse> = emptyList(),
        ): PostResponse =
            PostResponse(
                id = post.id!!,
                userId = post.userId,
                title = post.title,
                content = post.content,
                imageUrl = post.imageUrl,
                reactions =
                    PostReactions(
                        like =
                            LikeReaction(
                                count = likeCount,
                                isLiked = isLiked,
                            ),
                        comment =
                            CommentReaction(
                                count = commentCount,
                                items = comments,
                            ),
                    ),
                createdAt = TimeUtils.toIsoString(post.createdAt),
                updatedAt = TimeUtils.toIsoString(post.updatedAt),
            )
    }
}
