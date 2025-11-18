package oliveyoung.community.domain.user.service

import oliveyoung.community.domain.user.dto.request.RegisterRequest
import oliveyoung.community.domain.user.entity.Role
import oliveyoung.community.domain.user.entity.User
import oliveyoung.community.domain.user.repository.UserRepository
import oliveyoung.community.infrastructure.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * UserService 구현체
 *
 * @Service: Spring이 빈으로 등록
 * UserService 인터페이스의 구현체로 자동 주입됨
 */
@Service
@Transactional(readOnly = true)
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Transactional
    fun register(request: RegisterRequest): User {
        // 이메일 중복 체크
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already exists")
        }

        // 유저명 중복 체크
        if (userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("Username already exists")
        }

        // 유저 생성
        val user =
            User(
                username = request.username,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                imageUrl = request.image_url,
                role = request.role ?: Role.USER,
            )

        val savedUser: User = userRepository.insert(user)
        return savedUser
    }
}
