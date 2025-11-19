package oliveyoung.community.domain.feed.service

import oliveyoung.community.common.dto.CursorPaginationParams
import oliveyoung.community.domain.feed.dto.FeedResponse

/**
 * Feed Service 인터페이스
 */
interface FeedService {
    /**
     * 피드 조회 (커서 기반 페이지네이션)
     *
     * @param userId 조회하는 사용자 ID (null이면 비로그인 사용자)
     * @param params 커서 페이지네이션 파라미터
     * @return 피드 응답 (게시글 목록 + 커서 정보)
     */
    fun getFeed(
        userId: Long?,
        params: CursorPaginationParams,
    ): FeedResponse
}
