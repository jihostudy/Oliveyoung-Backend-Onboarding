-- 인덱스 자동 생성 스크립트
-- 애플리케이션 시작 시 자동으로 실행됩니다.
-- 이미 존재하는 인덱스는 에러가 발생하지만 무시됩니다.

-- ========== Posts 테이블 인덱스 ==========

-- user_id, deleted_at, id DESC 복합 인덱스 (피드 조회 최적화)
-- FeedRepository.findFeedByUserId() 쿼리 최적화
-- 사용 쿼리: WHERE user_id = ? AND deleted_at IS NULL ORDER BY id DESC
CREATE INDEX idx_posts_user ON posts(user_id, deleted_at, id DESC);

-- ========== Comments 테이블 인덱스 ==========

-- post_id, deleted_at 복합 인덱스 (댓글 조회 최적화)
-- PostDetailRepository.findCommentsByPostIdOrderByCreatedAtDesc() 쿼리 최적화
-- 사용 쿼리: WHERE post_id = ? AND deleted_at IS NULL ORDER BY created_at DESC
CREATE INDEX idx_comments_post_deleted ON comments(post_id, deleted_at);

