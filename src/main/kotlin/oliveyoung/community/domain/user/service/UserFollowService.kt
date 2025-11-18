package oliveyoung.community.domain.user.service

import oliveyoung.community.common.response.pagination.cursor.CursorPaginationParams
import oliveyoung.community.common.response.pagination.cursor.CursorPaginationResponse
import oliveyoung.community.domain.user.dto.response.UserResponse
import oliveyoung.community.domain.user.entity.Follow
import oliveyoung.community.domain.user.repository.UserFollowRepository
import oliveyoung.community.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * UserFollowService 구현체
 *
 * @Service: Spring이 빈으로 등록
 * UserFollowService 인터페이스의 구현체로 자동 주입됨
 */
@Service
@Transactional(readOnly = true)
class UserFollowService(
    private val userFollowRepository: UserFollowRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 팔로우
     */
    @Transactional
    fun follow(
        followerId: Long,
        followingId: Long,
    ) {
        if (followerId == followingId) {
            throw IllegalArgumentException("Cannot follow yourself")
        }

        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw IllegalArgumentException("Already following this user")
        }

        if (!userRepository.existsById(followingId)) {
            throw NoSuchElementException("User not found with id: $followingId")
        }

        val follow =
            Follow(
                followerId = followerId,
                followingId = followingId,
                createdAt = LocalDateTime.now(),
            )

        // TODO: follows_history에 팔로우 기록 남기기 (비동기)

        userFollowRepository.save(follow)
    }

    /**
     * 언팔로우
     */
    @Transactional
    fun unfollow(
        followerId: Long,
        followingId: Long,
    ) {
        val follow =
            userFollowRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                ?: throw NoSuchElementException("Follow relationship not found")

        userFollowRepository.delete(follow)

        // TODO: follows_history에 언팔로우 기록 남기기 (비동기)
    }

    /**
     * 특정 유저의 팔로워 조회
     * - 무한 스크롤
     */
    fun getFollowersPaginated(
        userId: Long,
        params: CursorPaginationParams,
    ): CursorPaginationResponse<UserResponse> {
        val limit = params.size + 1

        val follows =
            if (params.nextCursor == null) {
                userFollowRepository.findByFollowingIdOrderByIdDesc(userId, limit)
            } else {
                userFollowRepository.findByFollowingIdAndIdLessThanOrderByIdDesc(userId, params.nextCursor, limit)
            }

        val hasNext = follows.size > params.size
        val items = if (hasNext) follows.dropLast(1) else follows

        val followerIds = items.map { it.followerId }
        val followers = userRepository.findAllById(followerIds)

        val userResponses = followers.map { UserResponse.Companion.from(it) }

        return CursorPaginationResponse(
            items = userResponses,
            nextCursor = if (hasNext) items.last().id else null,
            hasNext = hasNext,
        )
    }

    /**
     * 특정 유저의 팔로잉 조회
     * - 무한 스크롤
     */
    fun getFollowingsPaginated(
        userId: Long,
        params: CursorPaginationParams,
    ): CursorPaginationResponse<UserResponse> {
        val limit = params.size + 1

        val follows =
            if (params.nextCursor == null) {
                userFollowRepository.findByFollowerIdOrderByIdDesc(userId, limit)
            } else {
                userFollowRepository.findByFollowerIdAndIdLessThanOrderByIdDesc(userId, params.nextCursor, limit)
            }

        val hasNext = follows.size > params.size
        val items = if (hasNext) follows.dropLast(1) else follows

        val followingIds = items.map { it.followingId }
        val followings = userRepository.findAllById(followingIds)

        val userResponses = followings.map { UserResponse.Companion.from(it) }

        return CursorPaginationResponse(
            items = userResponses,
            nextCursor = if (hasNext) items.last().id else null,
            hasNext = hasNext,
        )
    }
}
