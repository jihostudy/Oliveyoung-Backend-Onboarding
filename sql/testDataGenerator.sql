-- ============================================
-- 대용량 테스트 데이터 생성 스크립트
-- ============================================
-- 규모:
--   - 사용자: 10,000명
--   - 팔로우: 2,000,000개 (평균 200명 팔로우)
--   - 게시글: 100,000개
--   - 좋아요: 10,000,000개 (게시글당 평균 100개)
--   - 댓글: 10,000,000개 (게시글당 평균 100개)
-- ============================================

-- ============================================
-- 1. 사용자 데이터 생성 (10,000명)
-- ============================================
DELIMITER $$

DROP PROCEDURE IF EXISTS generate_users$$
CREATE PROCEDURE generate_users()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE batch_size INT DEFAULT 1000;

START TRANSACTION;

WHILE i <= 10000 DO
        INSERT INTO users (username, email, password, image_url, role, created_at, updated_at)
        VALUES (
            CONCAT('user', i),
            CONCAT('user', i, '@example.com'),
            '$2a$10$dummyhashedpassword',
            CONCAT('https://example.com/images/user', i, '.jpg'),
            'USER',
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY),
            NOW()
        );

        SET i = i + 1;

        IF i % batch_size = 0 THEN
            COMMIT;
START TRANSACTION;
SELECT CONCAT('Progress: ', i, ' / 10,000') AS status;
END IF;
END WHILE;

COMMIT;
SELECT '✅ Users created: 10,000' AS result;
END$$

DELIMITER ;

-- 실행
CALL generate_users();

-- ============================================
-- 2. 팔로우 관계 생성 (2,000,000개)
-- ============================================
DELIMITER $$

DROP PROCEDURE IF EXISTS generate_follows$$
CREATE PROCEDURE generate_follows()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE follower_id INT;
    DECLARE following_id INT;
    DECLARE batch_size INT DEFAULT 10000;
    DECLARE inserted INT DEFAULT 0;

START TRANSACTION;

WHILE inserted < 2000000 DO
        SET follower_id = FLOOR(1 + RAND() * 10000);
        SET following_id = FLOOR(1 + RAND() * 10000);

        IF follower_id != following_id THEN
            INSERT IGNORE INTO follows (follower_id, following_id, created_at)
            VALUES (
                follower_id,
                following_id,
                DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 180) DAY)
            );

            IF ROW_COUNT() > 0 THEN
                SET inserted = inserted + 1;
END IF;
END IF;

        SET i = i + 1;

        IF i % batch_size = 0 THEN
            COMMIT;
START TRANSACTION;
SELECT CONCAT('Progress: ', inserted, ' / 2,000,000 (tried: ', i, ')') AS status;
END IF;
END WHILE;

COMMIT;
SELECT CONCAT('✅ Follows created: ', inserted) AS result;
END$$

DELIMITER ;

-- 실행
CALL generate_follows();

-- ============================================
-- 3. 게시글 생성 (100,000개)
-- ============================================
DELIMITER $$

DROP PROCEDURE IF EXISTS generate_posts$$
CREATE PROCEDURE generate_posts()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_id INT;
    DECLARE batch_size INT DEFAULT 1000;

START TRANSACTION;

WHILE i <= 100000 DO
        SET user_id = FLOOR(1 + RAND() * 10000);

INSERT INTO posts (user_id, title, content, image_url, created_at, updated_at)
VALUES (
           user_id,
           CONCAT('게시글 제목 #', i),
           CONCAT('게시글 내용입니다. ', REPEAT('Lorem ipsum dolor sit amet. ', 10)),
           IF(RAND() > 0.5, CONCAT('https://example.com/images/post', i, '.jpg'), NULL),
           DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY),
           NOW()
       );

SET i = i + 1;

        IF i % batch_size = 0 THEN
            COMMIT;
START TRANSACTION;
SELECT CONCAT('Progress: ', i, ' / 100,000') AS status;
END IF;
END WHILE;

COMMIT;
SELECT '✅ Posts created: 100,000' AS result;
END$$

DELIMITER ;

-- 실행
CALL generate_posts();

-- ============================================
-- 4. 좋아요 생성 (10,000,000개)
-- ============================================
DELIMITER $$

DROP PROCEDURE IF EXISTS generate_likes$$
CREATE PROCEDURE generate_likes()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE post_id INT;
    DECLARE user_id INT;
    DECLARE batch_size INT DEFAULT 10000;
    DECLARE inserted INT DEFAULT 0;

START TRANSACTION;

WHILE inserted < 10000000 DO
        SET post_id = FLOOR(1 + RAND() * 100000);
        SET user_id = FLOOR(1 + RAND() * 10000);

        INSERT IGNORE INTO likes (post_id, user_id, created_at)
        VALUES (
            post_id,
            user_id,
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY)
        );

        IF ROW_COUNT() > 0 THEN
            SET inserted = inserted + 1;
END IF;

        SET i = i + 1;

        IF i % batch_size = 0 THEN
            COMMIT;
START TRANSACTION;
SELECT CONCAT('Progress: ', inserted, ' / 10,000,000 (tried: ', i, ')') AS status;
END IF;
END WHILE;

COMMIT;
SELECT CONCAT('✅ Likes created: ', inserted) AS result;
END$$

DELIMITER ;

-- 실행
CALL generate_likes();

-- ============================================
-- 5. 댓글 생성 (10,000,000개)
-- ============================================
DELIMITER $$

DROP PROCEDURE IF EXISTS generate_comments$$
CREATE PROCEDURE generate_comments()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE post_id INT;
    DECLARE user_id INT;
    DECLARE batch_size INT DEFAULT 10000;

START TRANSACTION;

WHILE i <= 10000000 DO
        SET post_id = FLOOR(1 + RAND() * 100000);
        SET user_id = FLOOR(1 + RAND() * 10000);

INSERT INTO comments (post_id, user_id, content, created_at, updated_at)
VALUES (
           post_id,
           user_id,
           CONCAT('댓글 내용입니다 #', i, '. ', REPEAT('Test comment. ', 5)),
           DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY),
           NOW()
       );

SET i = i + 1;

        IF i % batch_size = 0 THEN
            COMMIT;
START TRANSACTION;
SELECT CONCAT('Progress: ', i, ' / 10,000,000') AS status;
END IF;
END WHILE;

COMMIT;
SELECT '✅ Comments created: 10,000,000' AS result;
END$$

DELIMITER ;

-- 실행
CALL generate_comments();

-- ============================================
-- 6. 통계 확인
-- ============================================
SELECT 'users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'follows', COUNT(*) FROM follows
UNION ALL
SELECT 'posts', COUNT(*) FROM posts
UNION ALL
SELECT 'likes', COUNT(*) FROM likes
UNION ALL
SELECT 'comments', COUNT(*) FROM comments;

-- 평균 팔로우 수
SELECT AVG(follow_count) AS avg_follows_per_user
FROM (
         SELECT follower_id, COUNT(*) AS follow_count
         FROM follows
         GROUP BY follower_id
     ) AS t;

-- 평균 좋아요 수
SELECT AVG(like_count) AS avg_likes_per_post
FROM (
         SELECT post_id, COUNT(*) AS like_count
         FROM likes
         GROUP BY post_id
     ) AS t;

-- 평균 댓글 수
SELECT AVG(comment_count) AS avg_comments_per_post
FROM (
         SELECT post_id, COUNT(*) AS comment_count
         FROM comments
         GROUP BY post_id
     ) AS t;
