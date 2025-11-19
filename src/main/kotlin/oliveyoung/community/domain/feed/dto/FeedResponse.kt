package oliveyoung.community.domain.feed.dto

import oliveyoung.community.common.dto.CursorPaginationResponse
import oliveyoung.community.domain.post.dto.response.PostResponse

/**
 * Feed 응답 DTO
 *
 * 커서 기반 페이지네이션으로 게시글 목록 반환
 */
typealias FeedResponse = CursorPaginationResponse<PostResponse>
