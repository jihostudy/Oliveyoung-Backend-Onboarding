package oliveyoung.community.domain.feed.service.impl

import oliveyoung.community.common.dto.CursorPaginationParams
import oliveyoung.community.common.dto.CursorPaginationResponse
import oliveyoung.community.domain.feed.dto.FeedResponse
import oliveyoung.community.domain.feed.repository.FeedRepository
import oliveyoung.community.domain.feed.service.FeedService
import oliveyoung.community.domain.post.dto.response.PostResponse
import oliveyoung.community.domain.post.repository.PostDetailRepository
import oliveyoung.community.domain.user.dto.response.UserResponse
import oliveyoung.community.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * FeedService 구현체
 *
 * 커서 기반 무한 스크롤 피드
 */
@Service
@Transactional(readOnly = true)
class FeedServiceImpl(
    private val feedRepository: FeedRepository,
    private val postDetailRepository: PostDetailRepository,
    private val userRepository: UserRepository,
) : FeedService {
    override fun getFeed(
        userId: Long?,
        params: CursorPaginationParams,
    ): FeedResponse {
        // size + 1개를 조회하여 hasNext 판단

        val posts =
            if (userId != null) {
                // 로그인 사용자: 팔로우한 사용자들의 게시글
                feedRepository.findFeedByUserId(
                    userId = userId,
                    cursor = params.nextCursor,
                    size = params.size + 1,
                )
            } else {
                // 비로그인 사용자: 전체 최신 게시글
                feedRepository.findPublicFeed(
                    cursor = params.nextCursor,
                    size = params.size + 1,
                )
            }

        // Post ID 목록 추출
        val postIds = posts.map { it.id!! }

        // 배치로 좋아요/댓글 수 조회
        val likeCountMap = postDetailRepository.countLikesByPostIds(postIds)
        val commentCountMap = postDetailRepository.countCommentsByPostIds(postIds)

        // 현재 사용자가 좋아요한 게시글 ID 목록
        val likedPostIds =
            userId
                ?.let {
                    postDetailRepository.findLikedPostIdsByUserId(it, postIds)
                }?.toSet() ?: emptySet()

        // User 정보 배치 조회
        val userIds = posts.map { it.userId }.distinct()
        val users = userRepository.findAllById(userIds)
        val userMap = users.associateBy { it.id!! }

        // PostResponse로 변환
        val postResponses =
            posts.map { post ->
                val user =
                    userMap[post.userId]
                        ?: throw NoSuchElementException("User not found with id: ${post.userId}")
                val userResponse = UserResponse.from(user)

                PostResponse.from(
                    post = post,
                    user = userResponse,
                    likeCount = likeCountMap[post.id!!] ?: 0,
                    isLiked = likedPostIds.contains(post.id),
                    commentCount = commentCountMap[post.id!!] ?: 0,
                    comments = emptyList(), // 피드에서는 댓글 목록 제외
                )
            }

        // 커서 페이지네이션 응답 생성
        return CursorPaginationResponse.of(
            items = postResponses,
            size = params.size,
            getCursor = { it.id },
        )
    }
}
