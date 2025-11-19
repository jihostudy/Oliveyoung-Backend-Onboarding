package oliveyoung.community.domain.post.service

import oliveyoung.community.domain.post.dto.request.CreatePostRequest
import oliveyoung.community.domain.post.dto.response.PostResponse

/**
 * Post Service 인터페이스
 */
interface PostService {
    /**
     * 게시글 생성
     */
    fun createPost(request: CreatePostRequest): PostResponse

    /**
     * 게시글 단건 조회
     * @param postId 게시글 ID
     * @param userId 현재 사용자 ID (좋아요 여부 확인용, nullable
     */
    fun getPost(
        postId: Long,
        userId: Long?,
    ): PostResponse

    /**
     * 게시글 목록 조회 (최대 20개)
     * @param postIds 게시글 ID 목록
     * @param userId 현재 사용자 ID (좋아요 여부 확인용, nullable)
     */
    fun getPosts(
        postIds: List<Long>,
        userId: Long?,
    ): List<PostResponse>
}
