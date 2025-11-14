-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '유저 고유 식별자',
    username VARCHAR(50) NOT NULL COMMENT '유저명 (로그인용)',
    email VARCHAR(100) NOT NULL COMMENT '이메일',
    password VARCHAR(255) NOT NULL COMMENT '해시된 비밀번호 (bcrypt)',
    image_url VARCHAR(500) NULL COMMENT 'AWS S3 프로필 이미지 URL',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT 'USER | ADMIN',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL COMMENT 'Soft Delete',

    UNIQUE KEY uk_username_deleted (username, deleted_at),
    UNIQUE KEY uk_email_deleted (email, deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='유저 정보를 저장하는 테이블';

-- Follows Table (현재 상태만)
CREATE TABLE IF NOT EXISTS follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '팔로우 관계 고유 식별자',
    follower_id BIGINT NOT NULL COMMENT '팔로우를 하는 유저 ID',
    following_id BIGINT NOT NULL COMMENT '팔로우를 받는 유저 ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '팔로우 시작일시',

    UNIQUE KEY uk_follow_relation (follower_id, following_id),
    CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_following FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_self_follow CHECK (follower_id != following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='유저 간 팔로우 관계 (현재 상태)';

-- Follows History Table (이력)
CREATE TABLE IF NOT EXISTS follows_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '팔로우 이력 고유 식별자',
    follower_id BIGINT NOT NULL COMMENT '팔로우를 하는 유저 ID',
    following_id BIGINT NOT NULL COMMENT '팔로우를 받는 유저 ID',
    action VARCHAR(20) NOT NULL COMMENT 'FOLLOW | UNFOLLOW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '액션 발생 일시',

    CONSTRAINT fk_history_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_following FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='팔로우 이력';

-- Posts Table (Optional - 추후 Post Service에서 관리)
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 고유 식별자',
    user_id BIGINT NOT NULL COMMENT '작성자 ID',
    content TEXT NOT NULL COMMENT '게시글 본문 내용',
    image_url VARCHAR(500) NULL COMMENT 'AWS S3 첨부 이미지 URL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL COMMENT 'Soft Delete',
ㅁ
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시글 정보';

-- Comments Table (Optional - 추후 Post Service에서 관리)
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 고유 식별자',
    post_id BIGINT NOT NULL COMMENT '댓글이 달린 게시글 ID',
    user_id BIGINT NOT NULL COMMENT '댓글 작성자 ID',
    content TEXT NOT NULL COMMENT '댓글 내용',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL COMMENT 'Soft Delete',

    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시글 댓글';

-- Likes Table (Optional - 추후 Post Service에서 관리)
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '좋아요 고유 식별자',
    user_id BIGINT NOT NULL COMMENT '좋아요를 누른 유저 ID',
    post_id BIGINT NOT NULL COMMENT '좋아요가 달린 게시글 ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 누른 일시',

    UNIQUE KEY uk_user_post_like (user_id, post_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시글 좋아요';
