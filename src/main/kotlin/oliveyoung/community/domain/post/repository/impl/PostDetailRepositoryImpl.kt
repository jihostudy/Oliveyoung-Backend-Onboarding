package oliveyoung.community.domain.post.repository.impl

import oliveyoung.community.domain.post.entity.Comment
import oliveyoung.community.domain.post.entity.Like
import oliveyoung.community.domain.post.repository.PostDetailRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

/**
 * PostDetailRepository JDBC Template 구현체
 *
 * 댓글과 좋아요를 하나의 Repository에서 관리
 */
@Repository
class PostDetailRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
) : PostDetailRepository {
    // ========== RowMapper 정의 ==========

    private val commentRowMapper =
        RowMapper { rs, _ ->
            Comment(
                id = rs.getLong("id"),
                postId = rs.getLong("post_id"),
                userId = rs.getLong("user_id"),
                content = rs.getString("content"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime(),
                deletedAt = rs.getTimestamp("deleted_at")?.toLocalDateTime(),
            )
        }

    private val likeRowMapper =
        RowMapper { rs, _ ->
            Like(
                id = rs.getLong("id"),
                postId = rs.getLong("post_id"),
                userId = rs.getLong("user_id"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            )
        }

    // ========== Comment 메서드 구현 ==========

    override fun insertComment(comment: Comment): Comment {
        require(comment.id == null) { "Cannot insert comment with existing id" }

        val sql =
            """
            INSERT INTO comments (post_id, user_id, content, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
            """.trimIndent()

        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setLong(1, comment.postId)
            ps.setLong(2, comment.userId)
            ps.setString(3, comment.content)
            ps
        }, keyHolder)

        val generatedId =
            keyHolder.key?.toLong()
                ?: throw IllegalStateException("Failed to get generated ID")

        return comment.copy(id = generatedId)
    }

    override fun findCommentsByPostIdOrderByCreatedAtDesc(postId: Long): List<Comment> {
        val sql =
            """
            SELECT * FROM comments 
            WHERE post_id = ? AND deleted_at IS NULL 
            ORDER BY created_at DESC
            """.trimIndent()
        return jdbcTemplate.query(sql, commentRowMapper, postId)
    }

    override fun countCommentsByPostId(postId: Long): Int {
        val sql =
            """
            SELECT COUNT(*) FROM comments 
            WHERE post_id = ? AND deleted_at IS NULL
            """.trimIndent()
        return jdbcTemplate.queryForObject(sql, Int::class.java, postId) ?: 0
    }

    override fun countCommentsByPostIds(postIds: List<Long>): Map<Long, Int> {
        if (postIds.isEmpty()) return emptyMap()

        val placeholders = postIds.joinToString(",") { "?" }
        val sql =
            """
            SELECT post_id, COUNT(*) as cnt 
            FROM comments 
            WHERE post_id IN ($placeholders) AND deleted_at IS NULL
            GROUP BY post_id
            """.trimIndent()

        val results =
            jdbcTemplate.query(sql, { rs, _ ->
                rs.getLong("post_id") to rs.getInt("cnt")
            }, *postIds.toTypedArray())

        return results.toMap()
    }

    // ========== Like 메서드 구현 ==========

    override fun insertLike(like: Like): Like {
        require(like.id == null) { "Cannot insert like with existing id" }

        val sql =
            """
            INSERT INTO likes (post_id, user_id, created_at)
            VALUES (?, ?, NOW())
            """.trimIndent()

        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setLong(1, like.postId)
            ps.setLong(2, like.userId)
            ps
        }, keyHolder)

        val generatedId =
            keyHolder.key?.toLong()
                ?: throw IllegalStateException("Failed to get generated ID")

        return like.copy(id = generatedId)
    }

    override fun deleteLikeByPostIdAndUserId(
        postId: Long,
        userId: Long,
    ) {
        val sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?"
        jdbcTemplate.update(sql, postId, userId)
    }

    override fun existsLikeByPostIdAndUserId(
        postId: Long,
        userId: Long,
    ): Boolean {
        val sql = "SELECT COUNT(*) FROM likes WHERE post_id = ? AND user_id = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, postId, userId) ?: 0
        return count > 0
    }

    override fun countLikesByPostId(postId: Long): Int {
        val sql = "SELECT COUNT(*) FROM likes WHERE post_id = ?"
        return jdbcTemplate.queryForObject(sql, Int::class.java, postId) ?: 0
    }

    override fun countLikesByPostIds(postIds: List<Long>): Map<Long, Int> {
        if (postIds.isEmpty()) return emptyMap()

        val placeholders = postIds.joinToString(",") { "?" }
        val sql =
            """
            SELECT post_id, COUNT(*) as cnt 
            FROM likes 
            WHERE post_id IN ($placeholders) 
            GROUP BY post_id
            """.trimIndent()

        val results =
            jdbcTemplate.query(sql, { rs, _ ->
                rs.getLong("post_id") to rs.getInt("cnt")
            }, *postIds.toTypedArray())

        return results.toMap()
    }

    override fun findLikedPostIdsByUserId(
        userId: Long,
        postIds: List<Long>,
    ): List<Long> {
        if (postIds.isEmpty()) return emptyList()

        val placeholders = postIds.joinToString(",") { "?" }
        val sql =
            """
            SELECT post_id 
            FROM likes 
            WHERE user_id = ? AND post_id IN ($placeholders)
            """.trimIndent()

        val params = listOf(userId) + postIds
        return jdbcTemplate.queryForList(sql, Long::class.java, *params.toTypedArray())
    }
}
