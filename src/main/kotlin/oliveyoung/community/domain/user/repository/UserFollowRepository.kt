package oliveyoung.community.domain.user.repository

import oliveyoung.community.domain.user.entity.Follow

/**
 * UserFollow 도메인 Repository 인터페이스
 *
 * DIP(Dependency Inversion Principle) 적용:
 * - 고수준 모듈(Service)이 저수준 모듈(Repository 구현체)에 의존하지 않음
 * - 둘 다 추상화(Interface)에 의존
 */
interface UserFollowRepository {
    /**
     * 팔로우 관계 저장
     * @param follow 저장할 팔로우 관계 (id는 null이어야 함)
     * @return 저장된 팔로우 관계 (생성된 id 포함)
     */
    fun save(follow: Follow): Follow

    /**
     * 팔로우 관계 삭제
     * @param follow 삭제할 팔로우 관계
     */
    fun delete(follow: Follow)

    /**
     * 팔로우 관계 존재 여부 확인
     * @param followerId 팔로우를 하는 유저 ID
     * @param followingId 팔로우를 받는 유저 ID
     * @return 존재 여부
     */
    fun existsByFollowerIdAndFollowingId(
        followerId: Long,
        followingId: Long,
    ): Boolean

    /**
     * 팔로우 관계 조회
     * @param followerId 팔로우를 하는 유저 ID
     * @param followingId 팔로우를 받는 유저 ID
     * @return 팔로우 관계 또는 null
     */
    fun findByFollowerIdAndFollowingId(
        followerId: Long,
        followingId: Long,
    ): Follow?

    /**
     * 특정 유저의 팔로워 목록 조회 (커서 페이지네이션 - 첫 페이지)
     * @param followingId 팔로우를 받는 유저 ID
     * @param limit 조회할 개수
     * @return 팔로우 관계 목록
     */
    fun findByFollowingIdOrderByIdDesc(
        followingId: Long,
        limit: Int,
    ): List<Follow>

    /**
     * 특정 유저의 팔로워 목록 조회 (커서 페이지네이션 - 다음 페이지)
     * @param followingId 팔로우를 받는 유저 ID
     * @param cursor 커서 (이 ID보다 작은 것만 조회)
     * @param limit 조회할 개수
     * @return 팔로우 관계 목록
     */
    fun findByFollowingIdAndIdLessThanOrderByIdDesc(
        followingId: Long,
        cursor: Long,
        limit: Int,
    ): List<Follow>

    /**
     * 특정 유저의 팔로잉 목록 조회 (커서 페이지네이션 - 첫 페이지)
     * @param followerId 팔로우를 하는 유저 ID
     * @param limit 조회할 개수
     * @return 팔로우 관계 목록
     */
    fun findByFollowerIdOrderByIdDesc(
        followerId: Long,
        limit: Int,
    ): List<Follow>

    /**
     * 특정 유저의 팔로잉 목록 조회 (커서 페이지네이션 - 다음 페이지)
     * @param followerId 팔로우를 하는 유저 ID
     * @param cursor 커서 (이 ID보다 작은 것만 조회)
     * @param limit 조회할 개수
     * @return 팔로우 관계 목록
     */
    fun findByFollowerIdAndIdLessThanOrderByIdDesc(
        followerId: Long,
        cursor: Long,
        limit: Int,
    ): List<Follow>

    /**
     * 특정 유저를 팔로우하는 유저 ID 목록 조회
     * @param userId 팔로우를 받는 유저 ID
     * @return 팔로워 ID 목록
     */
    fun findFollowerIdsByFollowingId(userId: Long): List<Long>

    /**
     * 특정 유저가 팔로우하는 유저 ID 목록 조회
     * @param userId 팔로우를 하는 유저 ID
     * @return 팔로잉 ID 목록
     */
    fun findFollowingIdsByFollowerId(userId: Long): List<Long>
}
