package oliveyoung.community.infrastructure.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoder {
    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String = bCryptPasswordEncoder.encode(rawPassword)

    fun matches(
        rawPassword: String,
        encodedPassword: String,
    ): Boolean = bCryptPasswordEncoder.matches(rawPassword, encodedPassword)
}
