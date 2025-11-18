package oliveyoung.community.domain.user.repository

import oliveyoung.community.domain.user.entity.User

/**
 * User 도메인 Repository 인터페이스
 *
 * DIP(Dependency Inversion Principle) 적용:
 * - 고수준 모듈(Service)이 저수준 모듈(Repository 구현체)에 의존하지 않음
 * - 둘 다 추상화(Interface)에 의존
 */
interface UserRepository {
    /**
     * 새로운 유저 생성
     * @param user 생성할 유저 (id는 null이어야 함)
     * @return 생성된 유저 (생성된 id 포함)
     */
    fun insert(user: User): User

    /**
     * ID로 유저 조회
     * @param id 유저 ID
     * @return 유저 또는 null
     */
    fun findById(id: Long): User?

    /**
     * 이메일로 유저 조회
     * @param email 이메일
     * @return 유저 또는 null
     */
    fun findByEmail(email: String): User?

    /**
     * 유저명으로 유저 조회
     * @param username 유저명
     * @return 유저 또는 null
     */
    fun findByUsername(username: String): User?

    /**
     * 여러 ID로 유저 배치 조회
     * @param ids 유저 ID 목록
     * @return 유저 목록
     */
    fun findAllById(ids: List<Long>): List<User>

    /**
     * ID로 유저 존재 여부 확인
     * @param id 유저 ID
     * @return 존재 여부
     */
    fun existsById(id: Long): Boolean
}
