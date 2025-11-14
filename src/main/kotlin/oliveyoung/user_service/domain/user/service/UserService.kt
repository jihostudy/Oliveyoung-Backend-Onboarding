package oliveyoung.user_service.domain.user.service

import oliveyoung.user_service.domain.user.dto.RegisterRequest
import oliveyoung.user_service.domain.user.dto.UserResponse

/**
 * User 도메인 Service 인터페이스
 * 
 * 비즈니스 로직의 추상화:
 * - Controller는 이 인터페이스에만 의존
 * - 구현체 변경 시 Controller 수정 불필요
 * - 테스트 시 Mock 구현체 주입 가능
 */
interface UserService {
    
    /**
     * 유저 회원가입
     * @param request 회원가입 요청 정보
     * @return 생성된 유저 정보
     * @throws IllegalArgumentException 이메일 또는 유저명 중복 시
     */
    fun register(request: RegisterRequest): UserResponse
}
