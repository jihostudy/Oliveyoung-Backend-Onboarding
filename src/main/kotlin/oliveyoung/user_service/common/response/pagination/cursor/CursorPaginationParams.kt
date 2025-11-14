package oliveyoung.user_service.common.response.pagination.cursor

data class CursorPaginationParams(
    val size: Int = 20,
    val nextCursor: Long? = null
)

