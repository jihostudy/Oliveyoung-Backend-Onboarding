package oliveyoung.community.domain.post.dto.response

import oliveyoung.community.common.util.TimeUtils
import oliveyoung.community.domain.post.entity.Comment
import oliveyoung.community.domain.user.dto.response.UserResponse

/**
 * 댓글 응답 DTO
 */
data class CommentResponse(
    val id: Long,
    val user: UserResponse,
    val postId: Long,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun from(
            comment: Comment,
            user: UserResponse,
        ): CommentResponse =
            CommentResponse(
                id = comment.id!!,
                user = user,
                postId = comment.postId,
                content = comment.content,
                createdAt = TimeUtils.toIsoString(comment.createdAt),
                updatedAt = TimeUtils.toIsoString(comment.updatedAt),
            )
    }
}
