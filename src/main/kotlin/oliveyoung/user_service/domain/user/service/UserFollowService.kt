package oliveyoung.user_service.domain.user.service

import oliveyoung.user_service.common.response.pagination.cursor.CursorPaginationParams
import oliveyoung.user_service.common.response.pagination.cursor.CursorPaginationResponse
import oliveyoung.user_service.domain.user.dto.UserResponse

/**
 * UserFollow 도메인 Service 인터페이스
 * 
 * DIP(Dependency Inversion Principle) 적용:
 * - 고수준 모듈(Controller)이 저수준 모듈(Service 구현체)에 의존하지 않음
 * - 둘 다 추상화(Interface)에 의존
 */
interface UserFollowService {
    
    /**
     * 팔로우
     * @param followerId 팔로우를 하는 유저 ID
     * @param followingId 팔로우를 받는 유저 ID
     * @throws IllegalArgumentException 자기 자신을 팔로우하거나 이미 팔로우 중인 경우
     * @throws NoSuchElementException 팔로우할 유저가 존재하지 않는 경우
     */
    fun follow(followerId: Long, followingId: Long)
    
    /**
     * 언팔로우
     * @param followerId 팔로우를 하는 유저 ID
     * @param followingId 팔로우를 받는 유저 ID
     * @throws NoSuchElementException 팔로우 관계가 존재하지 않는 경우
     */
    fun unfollow(followerId: Long, followingId: Long)
    
    /**
     * 특정 유저의 팔로워 조회 (무한 스크롤)
     * @param userId 조회할 유저 ID
     * @param params 커서 페이지네이션 파라미터
     * @return 팔로워 목록 (커서 페이지네이션)
     */
    fun getFollowersPaginated(
        userId: Long, 
        params: CursorPaginationParams
    ): CursorPaginationResponse<UserResponse>
    
    /**
     * 특정 유저의 팔로잉 조회 (무한 스크롤)
     * @param userId 조회할 유저 ID
     * @param params 커서 페이지네이션 파라미터
     * @return 팔로잉 목록 (커서 페이지네이션)
     */
    fun getFollowingsPaginated(
        userId: Long, 
        params: CursorPaginationParams
    ): CursorPaginationResponse<UserResponse>
}
