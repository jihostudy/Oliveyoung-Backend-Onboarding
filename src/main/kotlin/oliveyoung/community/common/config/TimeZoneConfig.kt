package oliveyoung.community.common.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.util.TimeZone

/**
 * Timezone 설정
 *
 * 서버의 기본 시간대를 UTC로 설정
 * - 모든 LocalDateTime.now()는 UTC 기준
 * - DB 저장/조회도 UTC 기준
 */
@Configuration
class TimeZoneConfig {
    @PostConstruct
    fun init() {
        // JVM의 기본 시간대를 UTC로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}
