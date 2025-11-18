package oliveyoung.community.common.response.pagination.cursor

data class CursorPaginationResponse<T>(
    val items: List<T>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
