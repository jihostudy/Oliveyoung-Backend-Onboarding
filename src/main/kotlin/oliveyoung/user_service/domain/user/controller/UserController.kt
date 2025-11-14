package oliveyoung.user_service.domain.user.controller

import jakarta.validation.Valid
import oliveyoung.user_service.domain.user.dto.*
import oliveyoung.user_service.domain.user.service.UserService
import oliveyoung.user_service.presentation.response.ApiResponse
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
    
    /**
     * 회원가입
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): ApiResponse<UserResponse> {
        val user : UserResponse = userService.register(request)
        return ApiResponse.success(user, "회원가입이 완료되었습니다")
    }
}
