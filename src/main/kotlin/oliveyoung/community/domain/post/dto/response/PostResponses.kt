package oliveyoung.community.domain.post.dto.response

import oliveyoung.community.domain.post.entity.Comment
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
                createdAt = post.createdAt.toString(),
                updatedAt = post.updatedAt.toString(),
            )
    }
}

/**
 * 게시글 반응 정보
 */
data class PostReactions(
    val like: LikeReaction,
    val comment: CommentReaction,
)

/**
 * 좋아요 반응 정보
 */
data class LikeReaction(
    val count: Int,
    val isLiked: Boolean,
)

/**
 * 댓글 반응 정보
 */
data class CommentReaction(
    val count: Int,
    val items: List<CommentResponse>,
)

/**
 * 댓글 응답 DTO
 */
data class CommentResponse(
    val id: Long,
    val userId: Long,
    val postId: Long,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun from(comment: Comment): CommentResponse =
            CommentResponse(
                id = comment.id!!,
                userId = comment.userId,
                postId = comment.postId,
                content = comment.content,
                createdAt = comment.createdAt.toString(),
                updatedAt = comment.updatedAt.toString(),
            )
    }
}
