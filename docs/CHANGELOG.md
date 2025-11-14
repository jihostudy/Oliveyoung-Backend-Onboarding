# User Service - 변경 이력

## Version 0.0.2 (2024-11-13) - JDBC Template 마이그레이션 ✅

### 🔄 주요 변경사항

#### ORM 변경: JPA → JDBC Template
- **제거**: Spring Data JPA, Hibernate  
- **추가**: Spring JDBC, JdbcTemplate
- **이유**: 요구사항 준수 (JDBC Template 사용)

#### 의존성 변경
```gradle
// 제거
- spring-boot-starter-data-jpa
- kotlin-plugin-jpa

// 추가
+ spring-boot-starter-jdbc
+ kotlinx-coroutines-core (비동기 처리 준비)
+ springdoc-openapi-starter-webmvc-ui (Swagger)
```

### 📚 새로 추가된 기능

#### 1. Swagger/OpenAPI 통합 🎯
- **URL**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/api-docs
- **기능**:
  - 모든 API 자동 문서화
  - "Try it out"으로 직접 테스트
  - Request/Response 스키마 확인
  - Postman 대체 가능

#### 2. SQL 스키마 자동 생성
- **파일**: `src/main/resources/schema.sql`
- **실행**: 애플리케이션 시작 시 자동
- **테이블**: users, follows

#### 3. 비동기 처리 준비
- Kotlin Coroutines 의존성 추가
- 향후 외부 API 호출용

### 🏗️ 아키텍처 변경

#### Before (JPA)
```kotlin
@Entity
class User(...) { 
    fun incrementFollowerCount() { ... }
}

interface UserRepository : JpaRepository<User, Long>
```

#### After (JDBC Template)
```kotlin
data class User(...)  // Plain Data Class

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {
    fun save(user: User): User { ... }
    fun findById(id: Long): User? { ... }
}
```

### 📋 JDBC Template 구현 상세

#### UserRepository 메서드
```kotlin
- save(): INSERT/UPDATE (GeneratedKeyHolder 사용)
- findById(), findByEmail(): 단건 조회
- findAllById(): 배치 조회 (IN 절)
- existsByEmail/Username(): COUNT 쿼리
- increment/decrementFollowerCount(): UPDATE 쿼리
```

#### FollowRepository 메서드
```kotlin
- 커서 페이지네이션 SQL:
  SELECT * FROM follows 
  WHERE following_id = ? AND id < ?
  ORDER BY id DESC LIMIT ?
```

### 🗄️ 데이터베이스 스키마

**users 테이블:**
- PRIMARY KEY, AUTO_INCREMENT
- UNIQUE (email, username)
- INDEX (email, username)
- ON UPDATE CURRENT_TIMESTAMP

**follows 테이블:**
- UNIQUE KEY (follower_id, following_id)
- INDEX 2개
- FOREIGN KEY with CASCADE

### ⚙️ 설정 (application.yml)

```yaml
spring:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: alpha

logging:
  level:
    org.springframework.jdbc.core: DEBUG  # SQL 로깅
```

### 🚀 실행 방법

```bash
# 1. DB 생성
CREATE DATABASE sns_db;

# 2. 빌드 & 실행
./gradlew bootRun

# 3. Swagger 접속
open http://localhost:8081/swagger-ui.html
```

### 📊 JPA vs JDBC Template

| 항목 | JPA | JDBC Template |
|------|-----|---------------|
| 성능 | ⚠️ Hibernate overhead | ✅ 빠름 |
| SQL 제어 | ❌ 간접적 | ✅ 명시적 |
| 코드량 | ✅ 적음 | ⚠️ 많음 |
| N+1 문제 | ⚠️ 주의 필요 | ✅ 없음 |

### 🧪 테스트

**기존 Postman 테스트 유지:**
- 모든 API 동일하게 동작
- 엔드포인트 변경 없음

**Swagger 테스트 (추천):**
1. http://localhost:8081/swagger-ui.html
2. API 선택 → Try it out
3. Execute로 바로 테스트!

### 📝 다음 계획

- [ ] 비동기 외부 서비스 호출
- [ ] Redis 캐싱
- [ ] 통합 테스트
- [ ] Connection Pool 튜닝

---

## Version 0.0.1 (2024-11-13) - 초기 구현

[이전 버전 내용 유지...]
