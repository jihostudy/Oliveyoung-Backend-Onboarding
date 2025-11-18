package oliveyoung.community.domain.post.repository

import oliveyoung.community.domain.post.entity.Post

/**
 * Post Repository 인터페이스
 */
interface PostRepository {
    /**
     * 게시글 생성
     */
    fun insert(post: Post): Post

    /**
     * 게시글 단건 조회
     */
    fun findById(id: Long): Post?

    /**
     * 게시글 목록 조회 (최대 20개, 요청 순서 유지)
     */
    fun findAllById(ids: List<Long>): List<Post>

    /**
     * 게시글 존재 여부 확인
     */
    fun existsById(id: Long): Boolean
}
