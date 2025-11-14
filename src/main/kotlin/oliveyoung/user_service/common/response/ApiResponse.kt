package oliveyoung.user_service.presentation.response

data class ApiResponse<T>(
    val data: T?,
    val message: String,
    val code: String
) {
    companion object {
        fun <T> success(data: T?, message: String = "요청이 성공했습니다"): ApiResponse<T> {
            return ApiResponse(
                data = data,
                message = message,
                code = "SUCCESS"
            )
        }
        
        fun <T> error(message: String, code: String = "INVALID_REQUEST"): ApiResponse<T?> {
            return ApiResponse(
                data = null,
                message = message,
                code = code
            )
        }
    }
}
