package oliveyoung.community.common.response.pagination.cursor

data class CursorPaginationParams(
    val size: Int = 20,
    val nextCursor: Long? = null,
)
