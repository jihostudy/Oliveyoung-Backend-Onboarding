# 🌍 국제화 및 시간대 처리 가이드

## 📋 국제화 규정

### 핵심 원칙

1. **클라이언트**: 어느 시간대에 있든 **UTC 시간으로 변환**하여 서버에 전송
2. **서버**: 전달받은 **UTC 시간으로 저장** 및 처리
3. **응답**: 클라이언트에 **UTC 시간 그대로 반환** (ISO 8601 형식)
4. **클라이언트**: 받은 UTC 시간을 **로컬 시간대로 변환**하여 표시

---

## 🚨 문제점 분석

### Before (잘못된 구현)

```yaml
# application.yml
datasource:
  url: jdbc:mysql://...?serverTimezone=Asia/Seoul  # ❌ 한국 시간대
```

```kotlin
// Entity
val createdAt: LocalDateTime = LocalDateTime.now()  // ❌ 서버의 로컬 시간대 사용
```

```kotlin
// Response DTO
createdAt = post.createdAt.toString()  
// ❌ 출력: 2025-11-19T09:17:26 (시간대 정보 없음, KST로 추정)
```

**문제점:**
- 서버가 한국에 있으면 KST(UTC+9) 시간으로 저장됨
- 시간대 정보 없이 반환하여 클라이언트가 UTC인지 KST인지 알 수 없음
- 다른 시간대의 클라이언트에서 오해 가능

### After (올바른 구현)

```yaml
# application.yml
datasource:
  url: jdbc:mysql://...?serverTimezone=UTC  # ✅ UTC 시간대

jackson:
  time-zone: UTC  # ✅ JSON 직렬화 시 UTC
```

```kotlin
// TimeZoneConfig
TimeZone.setDefault(TimeZone.getTimeZone("UTC"))  // ✅ JVM 기본 시간대 UTC
```

```kotlin
// Response DTO
createdAt = TimeUtils.toIsoString(post.createdAt)
// ✅ 출력: 2025-11-19T00:17:26Z (UTC 명시)
```

---

## ✅ 구현된 설정

### 1. application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://...?serverTimezone=UTC  # MySQL UTC 설정
  
  jackson:
    time-zone: UTC  # Jackson JSON 직렬화 UTC
    serialization:
      write-dates-as-timestamps: false  # ISO 8601 형식 사용
```

### 2. TimeZoneConfig (JVM 기본 시간대)

```kotlin
@Configuration
class TimeZoneConfig {
    @PostConstruct
    fun init() {
        // JVM의 기본 시간대를 UTC로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}
```

**효과:**
- `LocalDateTime.now()` → UTC 기준으로 동작
- `new Date()` → UTC 기준으로 동작

### 3. TimeUtils (시간 유틸리티)

```kotlin
object TimeUtils {
    /**
     * 현재 UTC 시간 반환
     */
    fun nowUtc(): LocalDateTime {
        return LocalDateTime.now(ZoneOffset.UTC)
    }
    
    /**
     * ISO 8601 형식의 UTC 문자열로 변환
     * 예: 2025-11-19T00:17:26Z
     */
    fun toIsoString(dateTime: LocalDateTime): String {
        return ZonedDateTime.of(dateTime, ZoneOffset.UTC).toString()
    }
}
```

### 4. Response DTO (ISO 8601 형식)

```kotlin
data class PostResponse(
    val createdAt: String,  // "2025-11-19T00:17:26Z"
    val updatedAt: String   // "2025-11-19T00:17:26Z"
) {
    companion object {
        fun from(post: Post): PostResponse {
            return PostResponse(
                createdAt = TimeUtils.toIsoString(post.createdAt),
                updatedAt = TimeUtils.toIsoString(post.updatedAt)
            )
        }
    }
}
```

---

## 📊 Before / After 비교

### 시나리오: 한국(KST, UTC+9)에서 게시글 작성

| 항목 | Before (잘못됨) | After (올바름) |
|------|-----------------|----------------|
| **클라이언트 시간** | 2025-11-19 09:17:26 (KST) | 2025-11-19 09:17:26 (KST) |
| **서버 저장 시간** | 2025-11-19 09:17:26 (KST) ❌ | 2025-11-19 00:17:26 (UTC) ✅ |
| **응답 형식** | `"2025-11-19T09:17:26"` ❌ | `"2025-11-19T00:17:26Z"` ✅ |
| **미국 사용자 표시** | 2025-11-19 09:17:26? 🤔 | 2025-11-18 19:17:26 (EST) ✅ |

---

## 🎯 사용 가이드

### 1. Entity에서 시간 사용

```kotlin
// ❌ Bad: LocalDateTime.now() 직접 사용
data class Post(
    val createdAt: LocalDateTime = LocalDateTime.now()
)

// ✅ Good: TimeUtils 사용 (명시적)
data class Post(
    val createdAt: LocalDateTime = TimeUtils.nowUtc()
)

// ✅ Good: DB의 NOW() 사용 (Repository에서 처리)
// Entity는 기본값만 제공, 실제로는 Repository에서 NOW()로 덮어씀
data class Post(
    val createdAt: LocalDateTime = LocalDateTime.now()  // 기본값일 뿐
)
```

**참고:** Repository에서 `NOW()`를 사용하므로 Entity의 기본값은 사용되지 않습니다.

```kotlin
// Repository
val sql = """
    INSERT INTO posts (user_id, title, content, created_at, updated_at)
    VALUES (?, ?, ?, NOW(), NOW())  -- MySQL의 NOW()는 UTC 기준
""".trimIndent()
```

### 2. Response DTO에서 시간 변환

```kotlin
// ✅ Good: ISO 8601 형식으로 변환
data class PostResponse(
    val createdAt: String
) {
    companion object {
        fun from(post: Post): PostResponse {
            return PostResponse(
                createdAt = TimeUtils.toIsoString(post.createdAt)
                // 출력: "2025-11-19T00:17:26Z"
            )
        }
    }
}
```

### 3. Request DTO에서 시간 수신

```kotlin
// 클라이언트가 UTC로 변환하여 전송한다고 가정
data class CreatePostRequest(
    val title: String,
    val content: String,
    val scheduledAt: LocalDateTime?  // 클라이언트가 UTC로 변환하여 전송
)
```

**클라이언트 예시 (JavaScript):**
```javascript
// 클라이언트에서 UTC로 변환
const scheduledAt = new Date('2025-11-19T09:17:26+09:00').toISOString();
// "2025-11-19T00:17:26.000Z"

fetch('/api/v1/posts', {
    method: 'POST',
    body: JSON.stringify({
        title: 'Test',
        content: 'Content',
        scheduledAt: scheduledAt  // UTC로 전송
    })
});
```

---

## 🔍 검증 방법

### 1. 현재 설정 확인

```kotlin
// TimeZone 확인
println("Default TimeZone: ${TimeZone.getDefault().id}")
// 출력: Default TimeZone: UTC

// LocalDateTime.now() 확인
println("Current Time: ${LocalDateTime.now()}")
// 출력: Current Time: 2025-11-19T00:17:26 (UTC 기준)
```

### 2. MySQL 시간대 확인

```sql
-- MySQL 시간대 확인
SELECT @@global.time_zone, @@session.time_zone;
-- 출력: +00:00, +00:00 (UTC)

-- 현재 시간 확인
SELECT NOW();
-- 출력: 2025-11-19 00:17:26 (UTC 기준)
```

### 3. API 응답 확인

```bash
# POST 게시글 생성
curl -X POST http://localhost:8081/api/v1/posts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Test Post",
    "content": "Test Content"
  }'

# 응답 확인
{
  "data": {
    "id": 1,
    "createdAt": "2025-11-19T00:17:26Z",  // ✅ Z 접미사로 UTC 명시
    "updatedAt": "2025-11-19T00:17:26Z"
  }
}
```

---

## 📝 체크리스트

### 서버 설정
- [x] `application.yml`에서 `serverTimezone=UTC` 설정
- [x] Jackson `time-zone: UTC` 설정
- [x] `TimeZoneConfig`에서 JVM 기본 시간대 UTC 설정

### 코드 구현
- [x] `TimeUtils` 유틸리티 클래스 생성
- [x] Response DTO에서 `TimeUtils.toIsoString()` 사용
- [x] Repository에서 `NOW()` 사용 (MySQL UTC 기준)

### 테스트
- [ ] MySQL 시간대 확인 (`SELECT @@session.time_zone`)
- [ ] API 응답에 'Z' 접미사 포함 확인
- [ ] 다른 시간대에서 접속 시 정상 동작 확인

---

## 🌐 클라이언트 가이드

### JavaScript/TypeScript

```typescript
// UTC 시간으로 변환하여 전송
const now = new Date();
const utcTime = now.toISOString();  // "2025-11-19T00:17:26.000Z"

// 서버에서 받은 UTC 시간을 로컬 시간으로 변환
const serverTime = "2025-11-19T00:17:26Z";
const localTime = new Date(serverTime);
console.log(localTime.toLocaleString());  // 로컬 시간대로 표시
```

### React 예시

```typescript
// 서버에서 받은 UTC 시간 표시
function PostCard({ post }) {
    const createdAt = new Date(post.createdAt);  // UTC 파싱
    
    return (
        <div>
            <p>작성일: {createdAt.toLocaleString('ko-KR', { 
                timeZone: 'Asia/Seoul' 
            })}</p>
            {/* 출력: 2025-11-19 오전 9:17:26 */}
        </div>
    );
}
```

---

## 🚨 주의사항

### 1. LocalDateTime vs ZonedDateTime

```kotlin
// ❌ Bad: 시간대 정보 없음
val time: LocalDateTime = LocalDateTime.now()
// 2025-11-19T00:17:26 (UTC? KST? 알 수 없음)

// ✅ Good: 시간대 정보 포함
val time: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
// 2025-11-19T00:17:26Z (명확히 UTC)
```

**권장사항:**
- DB 저장: `LocalDateTime` (DB 자체가 UTC로 설정되어 있으므로)
- API 응답: ISO 8601 문자열 (시간대 정보 포함)

### 2. MySQL TIMESTAMP vs DATETIME

```sql
-- ✅ Good: TIMESTAMP (자동으로 UTC 변환)
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

-- ⚠️ DATETIME은 시간대 변환 없음
created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
```

### 3. 절대 하지 말아야 할 것

```kotlin
// ❌ 절대 금지: 시간대 하드코딩
val kstTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))

// ❌ 절대 금지: 수동 시간 변환
val utcTime = kstTime.minusHours(9)

// ✅ 항상 UTC 사용
val utcTime = TimeUtils.nowUtc()
```

---

## 📚 참고 자료

- [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601)
- [Java Time API](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html)
- [MySQL Time Zones](https://dev.mysql.com/doc/refman/8.0/en/time-zone-support.html)

---

**🌍 이제 전 세계 어디서나 정확한 시간을 처리할 수 있습니다!**
