package oliveyoung.user_service.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    
    @Bean
    fun openAPI(): OpenAPI {
        val server = Server()
        server.url = "http://localhost:8081"
        server.description = "Local Server"
        
        val info = Info()
        info.title = "User Service API"
        info.description = "SNS 피드 시스템의 User Service API 문서"
        info.version = "v1.0.0"
        
        val openAPI = OpenAPI()
        openAPI.info = info
        openAPI.servers = listOf(server)
        
        return openAPI
    }
}
