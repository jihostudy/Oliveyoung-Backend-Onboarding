package oliveyoung.community.common.dto

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

/**
 * 커서 기반 페이지네이션 응답
 *
 * @param items 조회된 데이터 목록
 * @param nextCursor 다음 커서 (더 이상 데이터가 없으면 null)
 * @param hasNext 다음 데이터 존재 여부
 */
data class CursorPaginationResponse<T>(
    val items: List<T>,
    val nextCursor: Long?,
    val hasNext: Boolean,
) {
    companion object {
        /**
         * 페이지네이션 응답 생성
         *
         * @param items 조회된 데이터 목록
         * @param size 요청한 size
         * @param getCursor 아이템에서 커서를 추출하는 함수
         */
        fun <T> of(
            items: List<T>,
            size: Int,
            getCursor: (T) -> Long,
        ): CursorPaginationResponse<T> {
            val hasNext = items.size > size
            val resultItems = if (hasNext) items.dropLast(1) else items
            val nextCursor =
                if (hasNext && resultItems.isNotEmpty()) {
                    getCursor(resultItems.last())
                } else {
                    null
                }

            return CursorPaginationResponse(
                items = resultItems,
                nextCursor = nextCursor,
                hasNext = hasNext,
            )
        }
    }
}
