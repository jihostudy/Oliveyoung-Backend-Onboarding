package oliveyoung.user_service.domain.user.service

import oliveyoung.user_service.domain.user.dto.*
import oliveyoung.user_service.domain.user.entity.User
import oliveyoung.user_service.domain.user.entity.Role
import oliveyoung.user_service.domain.user.repository.UserRepository
import oliveyoung.user_service.infrastructure.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class UserService {
    
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    
    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already exists")
        }
        
        if (userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("Username already exists")
        }
        
        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            imageUrl = request.image_url,
            role = request.role ?: Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
        
        val savedUser = userRepository.insert(user)
        return UserResponse.from(savedUser)
    }

//    @Transactional
//    fun incrementFollowerCount(userId: Long) {
//        if (!userRepository.existsById(userId)) {
//            throw NoSuchElementException("User not found with id: $userId")
//        }
//        userRepository.incrementFollowerCount(userId)
//    }
//
//    @Transactional
//    fun decrementFollowerCount(userId: Long) {
//        if (!userRepository.existsById(userId)) {
//            throw NoSuchElementException("User not found with id: $userId")
//        }
//        userRepository.decrementFollowerCount(userId)
//    }
//
//    @Transactional
//    fun incrementFollowingCount(userId: Long) {
//        if (!userRepository.existsById(userId)) {
//            throw NoSuchElementException("User not found with id: $userId")
//        }
//        userRepository.incrementFollowingCount(userId)
//    }
//
//    @Transactional
//    fun decrementFollowingCount(userId: Long) {
//        if (!userRepository.existsById(userId)) {
//            throw NoSuchElementException("User not found with id: $userId")
//        }
//        userRepository.decrementFollowingCount(userId)
//    }
}
