package oliveyoung.community.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * 시간 관련 유틸리티
 *
 * 국제화 규정:
 * 1. 클라이언트는 UTC 시간으로 변환하여 서버에 전송
 * 2. 서버는 UTC 시간으로 저장 및 처리
 * 3. 클라이언트로 응답 시 UTC 시간 그대로 전송
 * 4. 클라이언트에서 로컬 시간대로 변환하여 표시
 */
object TimeUtils {
    /**
     * 현재 UTC 시간 반환
     *
     * LocalDateTime.now() 대신 사용 권장
     */
    fun nowUtc(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    /**
     * Instant를 UTC LocalDateTime으로 변환
     */
    fun toUtcDateTime(instant: Instant): LocalDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)

    /**
     * LocalDateTime을 Instant로 변환 (UTC 기준)
     */
    fun toInstant(dateTime: LocalDateTime): Instant = dateTime.toInstant(ZoneOffset.UTC)

    /**
     * ISO 8601 형식의 UTC 문자열로 변환
     * 예: 2025-11-19T00:17:26Z
     */
    fun toIsoString(dateTime: LocalDateTime): String = ZonedDateTime.of(dateTime, ZoneOffset.UTC).toString()
}
