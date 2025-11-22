package oliveyoung.community.common.response.pagination.cursor

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
