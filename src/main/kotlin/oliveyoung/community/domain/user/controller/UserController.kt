package oliveyoung.community.domain.user.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import oliveyoung.community.common.response.ApiResponse
import oliveyoung.community.domain.user.dto.request.RegisterRequest
import oliveyoung.community.domain.user.dto.response.UserListResponse
import oliveyoung.community.domain.user.dto.response.UserResponse
import oliveyoung.community.domain.user.entity.User
import oliveyoung.community.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * 사용자 관련 공개 API
 */
@RestController
@RequestMapping("/api/v1/user")
class UserController {
    @Autowired
    private lateinit var userService: UserService

    @Operation(
        summary = "회원가입",
        description = "새로운 사용자를 등록합니다",
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: RegisterRequest,
    ): ApiResponse<UserResponse> {
        // 회원가입 처리
        val savedUser: User = userService.register(request)

        // 응답 형태로 변환
        val user: UserResponse = UserResponse.from(savedUser)
        return ApiResponse.success(user, "회원가입이 완료되었습니다")
    }

    @Operation(
        summary = "테스트용: 모든 유저 조회 (ID 오름차순)",
        description = "모든 유저를 ID 오름차순으로 조회합니다",
    )
    @GetMapping("/test/list")
    fun getAllUsersOrderById(): ApiResponse<UserListResponse> {
        val users: List<User> = userService.getAllUsersOrderById()
        val userResponses: List<UserResponse> = users.map { UserResponse.from(it) }
        val response = UserListResponse(items = userResponses)
        return ApiResponse.success(response)
    }
}
