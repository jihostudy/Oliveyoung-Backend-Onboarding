package oliveyoung.community.domain.post.service.impl

import oliveyoung.community.domain.post.dto.request.CreateCommentRequest
import oliveyoung.community.domain.post.dto.response.CommentResponse
import oliveyoung.community.domain.post.entity.Comment
import oliveyoung.community.domain.post.entity.Like
import oliveyoung.community.domain.post.repository.PostDetailRepository
import oliveyoung.community.domain.post.repository.PostRepository
import oliveyoung.community.domain.post.service.PostDetailService
import oliveyoung.community.domain.user.dto.response.UserResponse
import oliveyoung.community.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * PostDetailService 구현체
 *
 * 댓글과 좋아요를 하나의 Service에서 관리
 */
@Service
@Transactional(readOnly = true)
class PostDetailServiceImpl(
    private val postDetailRepository: PostDetailRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : PostDetailService {
    // ========== Comment 메서드 구현 ==========

    @Transactional
    override fun createComment(
        postId: Long,
        request: CreateCommentRequest,
    ): CommentResponse {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw NoSuchElementException("Post not found with id: $postId")
        }

        val comment =
            Comment(
                postId = postId,
                userId = request.userId,
                content = request.content,
            )

        val savedComment = postDetailRepository.insertComment(comment)

        // User 정보 조회
        val user =
            userRepository.findById(request.userId)
                ?: throw NoSuchElementException("User not found with id: ${request.userId}")
        val userResponse = UserResponse.from(user)

        return CommentResponse.from(savedComment, userResponse)
    }

    override fun getComments(postId: Long): List<CommentResponse> {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw NoSuchElementException("Post not found with id: $postId")
        }

        val comments = postDetailRepository.findCommentsByPostIdOrderByCreatedAtDesc(postId)

        // User 정보 배치 조회
        val userIds = comments.map { it.userId }.distinct()
        val users = userRepository.findAllById(userIds)
        val userMap = users.associateBy { it.id!! }

        return comments.map { comment ->
            val user =
                userMap[comment.userId]
                    ?: throw NoSuchElementException("User not found with id: ${comment.userId}")
            val userResponse = UserResponse.from(user)
            CommentResponse.from(comment, userResponse)
        }
    }

    // ========== Like 메서드 구현 ==========

    @Transactional
    override fun createLike(
        postId: Long,
        userId: Long,
    ) {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw NoSuchElementException("Post not found with id: $postId")
        }

        // 이미 좋아요한 경우 무시 (중복 방지)
        if (postDetailRepository.existsLikeByPostIdAndUserId(postId, userId)) {
            return
        }

        val like =
            Like(
                postId = postId,
                userId = userId,
            )

        postDetailRepository.insertLike(like)
    }

    @Transactional
    override fun deleteLike(
        postId: Long,
        userId: Long,
    ) {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw NoSuchElementException("Post not found with id: $postId")
        }

        // 좋아요가 없는 경우 무시
        if (!postDetailRepository.existsLikeByPostIdAndUserId(postId, userId)) {
            return
        }

        postDetailRepository.deleteLikeByPostIdAndUserId(postId, userId)
    }
}
