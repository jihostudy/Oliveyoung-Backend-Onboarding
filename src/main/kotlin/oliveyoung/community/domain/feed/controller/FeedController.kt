package oliveyoung.community.domain.feed.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import oliveyoung.community.common.response.ApiResponse
import oliveyoung.community.common.response.pagination.cursor.CursorPaginationParams
import oliveyoung.community.domain.feed.dto.FeedResponse
import oliveyoung.community.domain.feed.service.FeedService
import org.springframework.web.bind.annotation.*

/**
 * Feed Controller
 *
 * 커서 기반 무한 스크롤 피드 API
 */
@Tag(name = "Feed", description = "피드 API")
@RestController
@RequestMapping("/api/v1/feed")
class FeedController(
    private val feedService: FeedService,
) {
    @Operation(
        summary = "피드 조회",
        description = "커서 기반 무한 스크롤로 피드를 조회합니다.",
    )
    @GetMapping
    fun getFeed(
        @Parameter(description = "조회하는 사용자 ID (선택, 미입력 시 비로그인)")
        @RequestParam(required = false) userId: Long?,
        @Parameter(description = "한 번에 조회할 개수 (기본 20, 최대 100)")
        @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "다음 커서 (첫 조회 시 미입력)")
        @RequestParam(required = false) nextCursor: Long?,
    ): ApiResponse<FeedResponse> {
        val params =
            CursorPaginationParams(
                size = size,
                nextCursor = nextCursor,
            )

        val feed = feedService.getFeed(userId, params)
        return ApiResponse.success(feed)
    }
}
