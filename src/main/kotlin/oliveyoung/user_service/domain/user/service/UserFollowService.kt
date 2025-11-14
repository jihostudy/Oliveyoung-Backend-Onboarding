package oliveyoung.user_service.domain.user.service

import oliveyoung.user_service.common.response.pagination.cursor.CursorPaginationParams
import oliveyoung.user_service.common.response.pagination.cursor.CursorPaginationResponse
import oliveyoung.user_service.domain.user.dto.UserResponse
import oliveyoung.user_service.domain.user.entity.Follow
import oliveyoung.user_service.domain.user.repository.UserFollowRepository
import oliveyoung.user_service.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Service
@Transactional(readOnly = true)
class UserFollowService {

    @Autowired
    private lateinit var userFollowRepository: UserFollowRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    /**
     * 팔로우
     */
    @Transactional
    fun follow(followerId: Long, followingId: Long) {
        if (followerId == followingId) {
            throw IllegalArgumentException("Cannot follow yourself")
        }

        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw IllegalArgumentException("Already following this user")
        }

        if (!userRepository.existsById(followingId)) {
            throw NoSuchElementException("User not found with id: $followingId")
        }

        val follow = Follow(
            followerId = followerId,
            followingId = followingId,
            createdAt = LocalDateTime.now(),
        )

        // TODO: follows_history에 언팔로우 기록 남기기 (비동기)

        userFollowRepository.save(follow)
    }

    /**
     * 언팔로우
     */
    @Transactional
    fun unfollow(followerId: Long, followingId: Long) {
        val follow = userFollowRepository.findByFollowerIdAndFollowingId(followerId, followingId)
            ?: throw NoSuchElementException("Follow relationship not found")

        userFollowRepository.delete(follow)

        // TODO: follows_history에 언팔로우 기록 남기기 (비동기)
    }

    /**
     * 특정 유저의 팔로워 조회
     * - 무한 스크롤
     */
    fun getFollowersPaginated(userId: Long, params: CursorPaginationParams): CursorPaginationResponse<UserResponse> {
        val limit = params.size + 1

        val follows = if (params.nextCursor == null) {
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
            hasNext = hasNext
        )
    }

    fun getFollowingsPaginated(userId: Long, params: CursorPaginationParams): CursorPaginationResponse<UserResponse> {
        val limit = params.size + 1

        val follows = if (params.nextCursor == null) {
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
            hasNext = hasNext
        )
    }
}