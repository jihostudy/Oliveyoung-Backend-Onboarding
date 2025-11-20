package oliveyoung.community.domain.post.service.impl

import oliveyoung.community.domain.post.dto.request.CreatePostRequest
import oliveyoung.community.domain.post.dto.response.CommentResponse
import oliveyoung.community.domain.post.dto.response.PostResponse
import oliveyoung.community.domain.post.entity.Post
import oliveyoung.community.domain.post.repository.PostDetailRepository
import oliveyoung.community.domain.post.repository.PostRepository
import oliveyoung.community.domain.post.service.PostService
import oliveyoung.community.domain.user.dto.response.UserResponse
import oliveyoung.community.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * PostService 구현체
 *
 * PostDetailRepository를 사용하여 댓글/좋아요 정보 조회
 */
@Service
@Transactional(readOnly = true)
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val postDetailRepository: PostDetailRepository,
    private val userRepository: UserRepository,
) : PostService {
    @Transactional
    override fun createPost(request: CreatePostRequest): PostResponse {
        val post =
            Post(
                userId = request.userId,
                title = request.title,
                content = request.content,
                imageUrl = request.imageUrl,
            )

        val savedPost = postRepository.insert(post)

        // User 정보 조회
        val user =
            userRepository.findById(request.userId)
                ?: throw NoSuchElementException("User not found with id: ${request.userId}")
        val userResponse = UserResponse.from(user)

        return PostResponse.from(
            post = savedPost,
            user = userResponse,
            likeCount = 0,
            isLiked = false,
            commentCount = 0,
            comments = emptyList(),
        )
    }

    override fun getPost(
        postId: Long,
        currentUserId: Long?,
    ): PostResponse {
        // 1. 게시글 조회
        val post =
            postRepository.findById(postId)
                ?: throw NoSuchElementException("Post not found with id: $postId")

        // 2. 좋아요 수 조회 (PostDetailRepository 사용)
        val likeCount = postDetailRepository.countLikesByPostId(postId)

        // 3. 현재 사용자의 좋아요 여부 확인
        val isLiked =
            currentUserId?.let {
                postDetailRepository.existsLikeByPostIdAndUserId(postId, it)
            } ?: false

        // 4. 댓글 목록 조회 (PostDetailRepository 사용)
        val comments = postDetailRepository.findCommentsByPostIdOrderByCreatedAtDesc(postId)
        val commentResponses = comments.map { CommentResponse.from(it) }

        // 5. 댓글 수
        val commentCount = comments.size

        // 6. User 정보 조회
        val user =
            userRepository.findById(post.userId)
                ?: throw NoSuchElementException("User not found with id: ${post.userId}")
        val userResponse = UserResponse.from(user)

        // 7. DTO 변환
        return PostResponse.from(
            post = post,
            user = userResponse,
            likeCount = likeCount,
            isLiked = isLiked,
            commentCount = commentCount,
            comments = commentResponses,
        )
    }

    override fun getPosts(
        postIds: List<Long>,
        currentUserId: Long?,
    ): List<PostResponse> {
        if (postIds.isEmpty()) return emptyList()
        if (postIds.size > 20) {
            throw IllegalArgumentException("Maximum 20 post IDs allowed")
        }

        // 1. 게시글 배치 조회
        val posts = postRepository.findAllById(postIds)
        val postMap = posts.associateBy { it.id!! }

        // 2. 좋아요 수 배치 조회 (PostDetailRepository 사용)
        val likeCountMap = postDetailRepository.countLikesByPostIds(postIds)

        // 3. 현재 사용자가 좋아요한 게시글 ID 목록 (PostDetailRepository 사용)
        val likedPostIds =
            currentUserId
                ?.let {
                    postDetailRepository.findLikedPostIdsByUserId(it, postIds)
                }?.toSet() ?: emptySet()

        // 4. 댓글 수 배치 조회 (PostDetailRepository 사용)
        val commentCountMap = postDetailRepository.countCommentsByPostIds(postIds)

        // 5. User 정보 배치 조회
        val userIds = posts.map { it.userId }.distinct()
        val users = userRepository.findAllById(userIds)
        val userMap = users.associateBy { it.id!! }

        // 6. DTO 변환 (요청 순서 유지)
        return postIds.mapNotNull { postId ->
            postMap[postId]?.let { post ->
                val user =
                    userMap[post.userId]
                        ?: throw NoSuchElementException("User not found with id: ${post.userId}")
                val userResponse = UserResponse.from(user)

                PostResponse.from(
                    post = post,
                    user = userResponse,
                    likeCount = likeCountMap[postId] ?: 0,
                    isLiked = likedPostIds.contains(postId),
                    commentCount = commentCountMap[postId] ?: 0,
                    comments = emptyList(), // 목록 조회 시에는 댓글 내용 제외
                )
            }
        }
    }
}
