package oliveyoung.community.common.response.pagination.cursor

/**
 * 커서 기반 페이지네이션 파라미터
 *
 * @param size 한 번에 조회할 데이터 수 (기본 20, 최대 100)
 * @param nextCursor 다음 커서 (첫 조회 시 null)
 */
data class CursorPaginationParams(
    val size: Int = 20,
    val nextCursor: Long? = null,
) {
    init {
        require(size in 1..100) { "Size must be between 1 and 100" }
    }
}
