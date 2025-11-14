package oliveyoung.user_service.domain.user.service

import oliveyoung.user_service.domain.user.dto.RegisterRequest
import oliveyoung.user_service.domain.user.dto.UserResponse
import oliveyoung.user_service.domain.user.entity.Role
import oliveyoung.user_service.domain.user.entity.User
import oliveyoung.user_service.domain.user.repository.UserRepository
import oliveyoung.user_service.infrastructure.security.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * UserService 구현체
 * 
 * @Service: Spring이 빈으로 등록
 * UserService 인터페이스의 구현체로 자동 주입됨
 */
@Service
@Transactional(readOnly = true)
class UserServiceImpl (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
)  : UserService {
    
    @Transactional
    override fun register(request: RegisterRequest): UserResponse {
        // 이메일 중복 체크
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already exists")
        }
        
        // 유저명 중복 체크
        if (userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("Username already exists")
        }
        
        // 유저 생성
        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            imageUrl = request.image_url,
            role = request.role ?: Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedUser = userRepository.insert(user)
        return UserResponse.from(savedUser)
    }
}
