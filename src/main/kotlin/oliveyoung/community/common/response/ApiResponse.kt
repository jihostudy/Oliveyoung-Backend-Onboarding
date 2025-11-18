package oliveyoung.community.presentation.response

data class ApiResponse<T>(
    val data: T?,
    val message: String,
    val code: String,
) {
    companion object {
        fun <T> success(
            data: T?,
            message: String = "요청이 성공했습니다",
        ): ApiResponse<T> =
            ApiResponse(
                data = data,
                message = message,
                code = "SUCCESS",
            )

        fun <T> error(
            message: String,
            code: String = "INVALID_REQUEST",
        ): ApiResponse<T?> =
            ApiResponse(
                data = null,
                message = message,
                code = code,
            )
    }
}
