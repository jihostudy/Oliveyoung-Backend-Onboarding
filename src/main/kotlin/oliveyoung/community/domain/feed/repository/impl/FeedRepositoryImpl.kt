package oliveyoung.community.domain.feed.repository.impl

import oliveyoung.community.domain.feed.repository.FeedRepository
import oliveyoung.community.domain.post.entity.Post
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * FeedRepository JDBC Template 구현체
 *
 * 커서 기반 페이지네이션 구현
 */
@Repository
class FeedRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
) : FeedRepository {
    private val postRowMapper =
        RowMapper { rs, _ ->
            Post(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                title = rs.getString("title"),
                content = rs.getString("content"),
                imageUrl = rs.getString("image_url"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime(),
                deletedAt = rs.getTimestamp("deleted_at")?.toLocalDateTime(),
            )
        }

    override fun findFeedByUserId(
        userId: Long,
        cursor: Long?,
        size: Int,
    ): List<Post> {
        val sql =
            if (cursor == null) {
                // 첫 페이지: 커서 조건 없음
                """
                SELECT DISTINCT p.*
                FROM posts p
                INNER JOIN follows f ON p.user_id = f.following_id
                WHERE f.follower_id = ?
                AND p.deleted_at IS NULL
                ORDER BY p.id DESC
                LIMIT ?
                """.trimIndent()
            } else {
                // 다음 페이지: 커서보다 작은 ID
                """
                SELECT DISTINCT p.*
                FROM posts p
                INNER JOIN follows f ON p.user_id = f.following_id
                WHERE f.follower_id = ?
                  AND p.id < ?
                  AND p.deleted_at IS NULL
                ORDER BY p.id DESC
                LIMIT ?
                """.trimIndent()
            }

        return if (cursor == null) {
            jdbcTemplate.query(sql, postRowMapper, userId, size)
        } else {
            jdbcTemplate.query(sql, postRowMapper, userId, cursor, size)
        }
    }

    override fun findPublicFeed(
        cursor: Long?,
        size: Int,
    ): List<Post> {
        val sql =
            if (cursor == null) {
                // 첫 페이지: 커서 조건 없음
                """
                SELECT *
                FROM posts
                WHERE deleted_at IS NULL
                ORDER BY id DESC
                LIMIT ?
                """.trimIndent()
            } else {
                // 다음 페이지: 커서보다 작은 ID
                """
                SELECT *
                FROM posts
                WHERE id < ?
                  AND deleted_at IS NULL
                ORDER BY id DESC
                LIMIT ?
                """.trimIndent()
            }

        return if (cursor == null) {
            jdbcTemplate.query(sql, postRowMapper, size)
        } else {
            jdbcTemplate.query(sql, postRowMapper, cursor, size)
        }
    }
}
