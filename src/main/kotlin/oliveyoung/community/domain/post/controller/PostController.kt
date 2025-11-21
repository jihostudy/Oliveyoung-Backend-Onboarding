package oliveyoung.community.domain.post.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import oliveyoung.community.common.response.ApiResponse
import oliveyoung.community.domain.post.dto.request.CreatePostRequest
import oliveyoung.community.domain.post.dto.response.PostListResponse
import oliveyoung.community.domain.post.dto.response.PostResponse
import oliveyoung.community.domain.post.service.impl.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/api/v1/posts")
class PostController {
    @Autowired
    private lateinit var postService: PostService

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(
        @Valid @RequestBody request: CreatePostRequest,
    ): ApiResponse<PostResponse> {
        val post = postService.createPost(request)
        return ApiResponse.success(post, "게시글이 생성되었습니다")
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 단일 게시글을 조회합니다")
    @GetMapping("/{postId}")
    fun getPost(
        @PathVariable postId: Long,
        @RequestParam(required = false) userId: Long?, // TODO: userId는 쿼리가 아닌, JWT에서 추출하도록 변경
    ): ApiResponse<PostResponse> {
        val post = postService.getPost(postId, userId)
        return ApiResponse.success(post)
    }

    @Operation(
        summary = "게시글 목록 조회",
        description = "게시글 ID 목록으로 여러 게시글을 조회합니다 (최대 20개, CSV 형태)",
    )
    @GetMapping
    fun getPosts(
        @RequestParam postIds: String, // CSV형태 -> "1,2,3,4,5"
        @RequestParam(required = false) userId: Long?,
    ): ApiResponse<PostListResponse> {
        val ids =
            postIds
                .split(",")
                .map { it.trim().toLong() }
                .take(20) // 최대 20개 제한

        val posts = postService.getPosts(ids, userId)
        val response = PostListResponse(items = posts)
        return ApiResponse.success(response)
    }
}
