# 백엔드 온보딩 프로젝트

Spring Boot와 Kotlin으로 구현된 커뮤니티 서비스의 사용자 및 게시글 관리 서비스입니다.

## 📋 목차

1. [프로젝트 구조](#1-프로젝트-구조)
2. [ERD 구조](#2-erd-구조)
3. [API 명세서](#3-api-명세서)
4. [실행 방법](#4-실행-방법)
5. [기술 스택](#5-기술-스택)
6. [주요 기능](#6-주요-기능)

---

## 1. 프로젝트 구조

```
src/main/kotlin/oliveyoung/community/
├── common/                          # 공통 모듈
│   ├── aspects/
│   │   └── PerformanceLoggingAspect.kt    # AOP 성능 측정
│   ├── config/
│   │   ├── SwaggerConfig.kt               # Swagger 설정
│   │   └── TimeZoneConfig.kt              # 시간대 설정 (UTC)
│   ├── dto/
│   │   └── CursorPagination.kt            # 커서 페이지네이션 DTO
│   ├── response/
│   │   ├── ApiResponse.kt                 # 공통 API 응답
│   │   └── pagination/                    # 페이지네이션 응답
│   ├── security/
│   │   └── PasswordEncoder.kt             # 비밀번호 암호화
│   └── util/
│       └── TimeUtils.kt                   # 시간 유틸리티
│
├── domain/                          # 도메인별 모듈
│   ├── feed/                        # 피드 도메인
│   │   ├── controller/
│   │   │   └── FeedController.kt          # 피드 API
│   │   ├── dto/
│   │   │   └── FeedResponse.kt
│   │   ├── repository/
│   │   │   ├── FeedRepository.kt
│   │   │   └── impl/
│   │   │       └── FeedRepositoryImpl.kt  # JDBC Template 구현
│   │   └── service/
│   │       ├── FeedService.kt
│   │       └── impl/
│   │           └── FeedServiceImpl.kt
│   │
│   ├── post/                        # 게시글 도메인
│   │   ├── controller/
│   │   │   ├── PostController.kt          # 게시글 API
│   │   │   └── PostDetailController.kt    # 댓글/좋아요 API
│   │   ├── dto/
│   │   │   ├── request/                   # 요청 DTO
│   │   │   └── response/                  # 응답 DTO
│   │   ├── entity/
│   │   │   ├── Post.kt
│   │   │   ├── Comment.kt
│   │   │   └── Like.kt
│   │   ├── repository/
│   │   │   ├── PostRepository.kt
│   │   │   ├── PostDetailRepository.kt
│   │   │   └── impl/                       # JDBC Template 구현
│   │   └── service/
│   │       ├── PostService.kt
│   │       ├── PostDetailService.kt
│   │       └── impl/
│   │
│   └── user/                        # 사용자 도메인
│       ├── controller/
│       │   ├── UserController.kt           # 사용자 API
│       │   └── UserFollowController.kt    # 팔로우 API
│       ├── dto/
│       │   ├── request/
│       │   └── response/
│       ├── entity/
│       │   ├── User.kt
│       │   ├── Follow.kt
│       │   └── Role.kt
│       ├── repository/
│       │   ├── UserRepository.kt
│       │   ├── UserFollowRepository.kt
│       │   └── impl/                       # JDBC Template 구현
│       └── service/
│           ├── UserService.kt
│           └── UserFollowService.kt
│
└── UserServiceApplication.kt        # 메인 애플리케이션
```

### 아키텍처 패턴

-   **계층형 아키텍처**: Controller → Service → Repository
-   **도메인 주도 설계 (DDD)**: 도메인별로 패키지 분리
-   **의존성 역전 원칙 (DIP)**: Repository 인터페이스와 구현체 분리
-   **JDBC Template**: JPA 대신 순수 JDBC Template 사용

---

## 2. ERD 구조

### 데이터베이스 스키마

```
┌─────────────────┐
│     users       │
├─────────────────┤
│ id (PK)         │
│ username        │
│ email           │
│ password        │
│ image_url       │
│ role            │
│ created_at      │
│ updated_at      │
│ deleted_at      │
└────────┬────────┘
         │
         │ 1:N
         │
    ┌────┴─────────────────────────────────────┐
    │                                           │
    │                                           │
┌───▼────────┐                          ┌──────▼──────┐
│   posts    │                          │   follows   │
├────────────┤                          ├─────────────┤
│ id (PK)    │                          │ id (PK)     │
│ user_id    │◄─────┐                   │ follower_id │
│ title      │      │                   │ following_id│
│ content    │      │                   │ created_at  │
│ image_url  │      │                   └─────────────┘
│ created_at │      │
│ updated_at │      │
│ deleted_at │      │
└─────┬──────┘      │
      │             │
      │ 1:N         │ 1:N
      │             │
┌─────▼──────┐ ┌───▼──────┐
│  comments  │ │   likes  │
├────────────┤ ├──────────┤
│ id (PK)    │ │ id (PK)  │
│ post_id    │ │ post_id  │
│ user_id    │ │ user_id  │
│ content    │ │ created_ │
│ created_at │ │   at     │
│ updated_at │ └──────────┘
│ deleted_at │
└────────────┘
```

### 테이블 관계

| 테이블  | 관계 | 참조 테이블 | 설명                              |
| ------- | ---- | ----------- | --------------------------------- |
| `users` | 1:N  | `posts`     | 사용자는 여러 게시글 작성 가능    |
| `users` | 1:N  | `comments`  | 사용자는 여러 댓글 작성 가능      |
| `users` | 1:N  | `likes`     | 사용자는 여러 좋아요 가능         |
| `users` | N:M  | `follows`   | 사용자 간 팔로우 관계             |
| `posts` | 1:N  | `comments`  | 게시글은 여러 댓글 가질 수 있음   |
| `posts` | 1:N  | `likes`     | 게시글은 여러 좋아요 받을 수 있음 |

### 주요 제약조건

-   **Soft Delete**: 모든 테이블에 `deleted_at` 컬럼으로 논리 삭제 구현
-   **UNIQUE 제약**:
    -   `users`: `(username, deleted_at)`, `(email, deleted_at)`
    -   `follows`: `(follower_id, following_id)`
    -   `likes`: `(user_id, post_id)`
-   **FOREIGN KEY**: CASCADE DELETE 적용

### 인덱스

**적용된 인덱스:**

-   `idx_posts_user` (posts): `(user_id, deleted_at, id DESC)` - 피드 조회 최적화
-   `idx_comments_post_deleted` (comments): `(post_id, deleted_at)` - 댓글 조회 최적화

**추가 권장 인덱스:**

-   `idx_comments_post_deleted_created` (comments): `(post_id, deleted_at, created_at DESC)`
-   `idx_likes_post_id` (likes): `(post_id)`
-   `idx_follows_follower_id` (follows): `(follower_id)`
-   `idx_follows_following_id` (follows): `(following_id)`
-   `idx_posts_deleted_id` (posts): `(deleted_at, id DESC)`

자세한 내용은 [PERFORMANCE_IMPROVEMENT_OPPORTUNITIES.md](./docs/PERFORMANCE_IMPROVEMENT_OPPORTUNITIES.md) 참고

---

## 3. API 명세서

### 3.1 User Service (사용자)

| Method | Endpoint                 | 설명                      | 인증 |
| ------ | ------------------------ | ------------------------- | ---- |
| POST   | `/api/v1/user`           | 회원가입                  | ❌   |
| GET    | `/api/v1/user/test/list` | 모든 유저 조회 (테스트용) | ❌   |

### 3.2 User Follow Service (팔로우)

| Method | Endpoint                                                           | 설명                            | 인증 |
| ------ | ------------------------------------------------------------------ | ------------------------------- | ---- |
| POST   | `/api/v1/user/{userId}/follow`                                     | 특정 유저 팔로우                | ❌   |
| DELETE | `/api/v1/user/{userId}/follow?followingUserId={id}`                | 특정 유저 언팔로우              | ❌   |
| GET    | `/api/v1/user/{userId}/followers?size={size}&nextCursor={cursor}`  | 팔로워 조회 (커서 페이지네이션) | ❌   |
| GET    | `/api/v1/user/{userId}/followings?size={size}&nextCursor={cursor}` | 팔로잉 조회 (커서 페이지네이션) | ❌   |

### 3.3 Post Service (게시글)

| Method | Endpoint                                          | 설명                         | 인증 |
| ------ | ------------------------------------------------- | ---------------------------- | ---- |
| POST   | `/api/v1/posts`                                   | 게시글 생성                  | ❌   |
| GET    | `/api/v1/posts/{postId}?userId={id}`              | 게시글 단건 조회             | ❌   |
| GET    | `/api/v1/posts?postIds={id1,id2,...}&userId={id}` | 게시글 목록 조회 (최대 20개) | ❌   |

### 3.4 Post Detail Service (댓글/좋아요)

| Method | Endpoint                                   | 설명           | 인증 |
| ------ | ------------------------------------------ | -------------- | ---- |
| POST   | `/api/v1/posts/{postId}/comments`          | 댓글 생성      | ❌   |
| GET    | `/api/v1/posts/{postId}/comments`          | 댓글 목록 조회 | ❌   |
| POST   | `/api/v1/posts/{postId}/likes?userId={id}` | 좋아요 생성    | ❌   |
| DELETE | `/api/v1/posts/{postId}/likes?userId={id}` | 좋아요 삭제    | ❌   |

### 3.5 Feed Service (피드)

| Method | Endpoint                                                   | 설명                          | 인증 |
| ------ | ---------------------------------------------------------- | ----------------------------- | ---- |
| GET    | `/api/v1/feed?userId={id}&size={size}&nextCursor={cursor}` | 피드 조회 (커서 페이지네이션) | ❌   |

**쿼리 파라미터:**

-   `userId`: 조회하는 사용자 ID (선택, 미입력 시 비로그인 사용자용 공개 피드)
-   `size`: 한 번에 조회할 개수 (기본 20, 최대 100)
-   `nextCursor`: 다음 커서 (첫 조회 시 미입력)

### API 응답 형식

모든 API는 다음 형식으로 응답합니다:

```json
{
  "success": true,
  "message": "성공 메시지",
  "data": { ... }
}
```

**에러 응답:**

```json
{
    "success": false,
    "message": "에러 메시지",
    "data": null
}
```

### Swagger UI

API 문서는 Swagger UI를 통해 확인할 수 있습니다:

-   **Swagger UI**: http://localhost:8081/swagger-ui.html
-   **API Docs (JSON)**: http://localhost:8081/api-docs

---

## 4. 실행 방법

### 4.1 사전 요구사항

-   **Java**: 17 이상
-   **MySQL**: 8.0 이상
-   **Gradle**: 프로젝트에 포함된 Gradle Wrapper 사용

### 4.2 데이터베이스 설정

1. **MySQL 데이터베이스 생성** (선택사항)

    ```sql
    CREATE DATABASE sns_db;
    ```

    > 참고: `application.yml`에 `createDatabaseIfNotExist=true` 설정이 있어 자동 생성됩니다.

2. **환경 변수 설정** (선택사항)

    ```bash
    export MYSQL_USERNAME=root
    export MYSQL_PASSWORD=your_password
    ```

    > 기본값: `username=root`, `password=빈 문자열`

### 4.3 애플리케이션 실행

**방법 1: Gradle Wrapper 사용 (권장)**

```bash
# 프로젝트 루트에서
./gradlew bootRun
```

**방법 2: 빌드 후 실행**

```bash
# 빌드
./gradlew build

# 실행
java -jar build/libs/onboarding-0.0.1-SNAPSHOT.jar
```

**방법 3: IDE에서 실행**

-   `UserServiceApplication.kt`를 실행

### 4.4 실행 확인

애플리케이션이 정상적으로 실행되면:

1. **서버 포트**: http://localhost:8081
2. **Swagger UI**: http://localhost:8081/swagger-ui.html
3. **Health Check**: 애플리케이션 로그 확인

### 4.5 데이터베이스 초기화

애플리케이션 시작 시 자동으로:

-   `schema.sql` 실행 → 테이블 생성
-   `indexes.sql` 실행 → 인덱스 생성

> 주의: `mode: always` 설정으로 매번 실행되지만, `IF NOT EXISTS`로 중복 생성 방지

---

## 5. 기술 스택

### Backend

-   **언어**: Kotlin 1.9.25
-   **프레임워크**: Spring Boot 3.5.7
-   **데이터베이스**: MySQL 8.0+
-   **데이터 접근**: Spring JDBC Template
-   **빌드 도구**: Gradle

### 주요 라이브러리

-   **Spring Boot Starter Web**: REST API
-   **Spring Boot Starter JDBC**: JDBC Template
-   **Spring Boot Starter Validation**: 요청 검증
-   **Spring Boot Starter AOP**: AOP (성능 측정)
-   **Spring Security Crypto**: 비밀번호 암호화 (bcrypt)
-   **Springdoc OpenAPI**: Swagger/OpenAPI 문서화
-   **Jackson Module Kotlin**: Kotlin JSON 직렬화

### 개발 도구

-   **ktlint**: Kotlin 코드 스타일 검사

---

## 6. 주요 기능

### 6.1 사용자 관리

-   회원가입 (비밀번호 bcrypt 암호화)
-   Soft Delete 지원
-   이메일/유저명 중복 체크

### 6.2 팔로우 기능

-   팔로우/언팔로우
-   팔로워/팔로잉 목록 조회 (커서 페이지네이션)
-   자기 자신 팔로우 방지

### 6.3 게시글 관리

-   게시글 생성/조회
-   게시글 목록 조회 (배치 조회, 최대 20개)
-   Soft Delete 지원

### 6.4 댓글/좋아요

-   댓글 생성/조회
-   좋아요 생성/삭제
-   배치 조회로 N+1 문제 해결

### 6.5 피드 기능

-   커서 기반 무한 스크롤
-   로그인 사용자: 팔로우한 사용자들의 게시글
-   비로그인 사용자: 전체 최신 게시글

### 6.6 성능 최적화

-   **N+1 문제 해결**: 배치 조회 패턴 적용
-   **인덱스 최적화**: 자주 조회되는 컬럼에 인덱스 적용
-   **AOP 성능 측정**: 각 레이어별 실행 시간 로깅
-   **커서 페이지네이션**: 대용량 데이터 효율적 조회

---

## 📚 추가 문서

-   [N+1 문제 해결 가이드](./docs/N+1_PROBLEM_SOLUTION.md)
-   [성능 개선 기회 분석](./docs/PERFORMANCE_IMPROVEMENT_OPPORTUNITIES.md)
-   [시간대 처리 가이드](./docs/TIMEZONE_GUIDE.md)
-   [변경 이력](./docs/CHANGELOG.md)

---

## 🧪 테스트

### 성능 테스트

```bash
# AOP 기반 성능 측정
./scripts/aop_performance_test.sh "측정 라벨"
```

### Swagger를 통한 API 테스트

1. http://localhost:8081/swagger-ui.html 접속
2. 원하는 API 선택
3. "Try it out" 클릭
4. 파라미터 입력 후 "Execute" 실행

---

## ⚙️ 설정

### 주요 설정 파일

-   `application.yml`: 애플리케이션 설정
-   `schema.sql`: 데이터베이스 스키마
-   `indexes.sql`: 인덱스 생성 스크립트

### 환경 변수

-   `MYSQL_USERNAME`: MySQL 사용자명 (기본: root)
-   `MYSQL_PASSWORD`: MySQL 비밀번호 (기본: 빈 문자열)

---

## 📝 참고사항

### 아키텍처 특징

1. **JDBC Template 사용**: JPA 대신 순수 JDBC Template으로 SQL 직접 제어
2. **도메인 주도 설계**: 도메인별로 패키지 분리
3. **의존성 역전**: Repository 인터페이스와 구현체 분리
4. **배치 조회 패턴**: N+1 문제 방지를 위한 배치 조회 적용

### 성능 최적화

-   인덱스 최적화로 조회 성능 개선
-   배치 조회로 쿼리 수 최소화
-   커서 페이지네이션으로 대용량 데이터 효율적 처리

### 향후 개선 사항

-   [ ] JWT 인증 추가
-   [ ] Redis 캐싱 도입
-   [ ] 비동기 처리 (팔로우 이력 저장)
-   [ ] 추가 인덱스 적용
-   [ ] Race Condition 해결 (INSERT IGNORE)

자세한 내용은 [PERFORMANCE_IMPROVEMENT_OPPORTUNITIES.md](./docs/PERFORMANCE_IMPROVEMENT_OPPORTUNITIES.md) 참고
