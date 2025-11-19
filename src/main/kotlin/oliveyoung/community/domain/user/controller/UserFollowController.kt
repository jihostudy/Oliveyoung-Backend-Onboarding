package oliveyoung.community.domain.user.controller

import jakarta.validation.Valid
import oliveyoung.community.common.response.ApiResponse
import oliveyoung.community.common.response.pagination.cursor.CursorPaginationParams
import oliveyoung.community.common.response.pagination.cursor.CursorPaginationResponse
import oliveyoung.community.domain.user.dto.*
import oliveyoung.community.domain.user.dto.request.FollowRequest
import oliveyoung.community.domain.user.dto.response.UserResponse
import oliveyoung.community.domain.user.service.UserFollowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * 팔로우 관련 공개 API
 */
@RestController
@RequestMapping("/api/v1/user/{userId}")
class UserFollowController {
    @Autowired
    private lateinit var userFollowService: UserFollowService

    /**
     * 팔로우
     */
    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun follow(
        @PathVariable userId: Long,
        @Valid @RequestBody request: FollowRequest,
    ) {
        userFollowService.follow(userId, request.followingUserId)
    }

    /**
     * 언팔로우
     */
    @DeleteMapping("/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unfollow(
        @PathVariable userId: Long,
        @RequestParam followingUserId: Long,
    ) {
        userFollowService.unfollow(userId, followingUserId)
    }

    /**
     * 특정 유저의 팔로워 조회 (무한 스크롤)
     */
    @GetMapping("/followers")
    fun getFollowers(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) nextCursor: Long?,
    ): ApiResponse<CursorPaginationResponse<UserResponse>> {
        val params = CursorPaginationParams(size, nextCursor)
        val followers = userFollowService.getFollowersPaginated(userId, params)
        return ApiResponse.success(followers)
    }

    /**
     * 특정 유저의 팔로잉 조회 (무한 스크롤)
     */
    @GetMapping("/followings")
    fun getFollowings(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) nextCursor: Long?,
    ): ApiResponse<CursorPaginationResponse<UserResponse>> {
        val params = CursorPaginationParams(size, nextCursor)
        val followings = userFollowService.getFollowingsPaginated(userId, params)
        return ApiResponse.success(followings)
    }
}
