package oliveyoung.community.domain.user.controller

import io.swagger.v3.oas.annotations.Operation
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

    @Operation(summary = "특정 유저 팔로우", description = "특정 유저를 팔로우합니다")
    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun follow(
        @PathVariable userId: Long,
        @Valid @RequestBody request: FollowRequest,
    ) {
        userFollowService.follow(userId, request.followingUserId)
    }

    @Operation(summary = "특정 유저 언팔로우", description = "특정 유저를 언팔로우합니다")
    @DeleteMapping("/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unfollow(
        @PathVariable userId: Long,
        @RequestParam followingUserId: Long,
    ) {
        userFollowService.unfollow(userId, followingUserId)
    }

    @Operation(summary = "특정 유저 팔로워 조회", description = "특정 유저의 팔로워를 커서 기반 무한 스크롤로 조회합니다")
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

    @Operation(summary = "특정 유저 팔로잉 조회", description = "특정 유저의 팔로잉을 커서 기반 무한 스크롤로 조회합니다")
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
