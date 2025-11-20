package oliveyoung.community.domain.post.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import oliveyoung.community.common.response.ApiResponse
import oliveyoung.community.domain.post.dto.request.CreateCommentRequest
import oliveyoung.community.domain.post.dto.response.CommentListResponse
import oliveyoung.community.domain.post.dto.response.CommentResponse
import oliveyoung.community.domain.post.service.PostDetailService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * PostDetail Controller
 *
 * 게시글 상세 정보 관련 (댓글, 좋아요)
 */
@Tag(name = "Post Detail", description = "게시글 상세")
@RestController
@RequestMapping("/api/v1/posts/{postId}")
class PostDetailController(
    private val postDetailService: PostDetailService,
) {
    // ========== 댓글 API ==========

    @Operation(summary = "댓글 생성", description = "게시글에 댓글을 생성합니다")
    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createComment(
        @PathVariable postId: Long,
        @Valid @RequestBody request: CreateCommentRequest,
    ): ApiResponse<CommentResponse> {
        val comment = postDetailService.createComment(postId, request)
        return ApiResponse.success(comment, "댓글이 생성되었습니다")
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글의 모든 댓글을 조회합니다")
    @GetMapping("/comments")
    fun getComments(
        @PathVariable postId: Long,
    ): ApiResponse<CommentListResponse> {
        val comments = postDetailService.getComments(postId)
        val response = CommentListResponse(items = comments)
        return ApiResponse.success(response)
    }

    // ========== 좋아요 API ==========

    @Operation(summary = "좋아요 생성", description = "게시글에 좋아요를 추가합니다")
    @PostMapping("/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createLike(
        @PathVariable postId: Long,
        @RequestParam userId: Long,
    ) {
        postDetailService.createLike(postId, userId)
    }

    @Operation(summary = "좋아요 삭제", description = "게시글의 좋아요를 취소합니다")
    @DeleteMapping("/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteLike(
        @PathVariable postId: Long,
        @RequestParam userId: Long,
    ) {
        postDetailService.deleteLike(postId, userId)
    }
}
