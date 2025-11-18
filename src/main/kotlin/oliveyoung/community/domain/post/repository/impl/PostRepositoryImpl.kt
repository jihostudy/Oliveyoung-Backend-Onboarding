package oliveyoung.community.domain.post.repository.impl

import oliveyoung.community.domain.post.entity.Post
import oliveyoung.community.domain.post.repository.PostRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

/**
 * PostRepository JDBC Template 구현체
 */
@Repository
class PostRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
) : PostRepository {
    private val rowMapper =
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

    override fun insert(post: Post): Post {
        require(post.id == null) { "Cannot insert post with existing id" }

        val sql =
            """
            INSERT INTO posts (user_id, title, content, image_url, created_at, updated_at)
            VALUES (?, ?, ?, ?, NOW(), NOW())
            """.trimIndent()

        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setLong(1, post.userId)
            ps.setString(2, post.title)
            ps.setString(3, post.content)
            ps.setString(4, post.imageUrl)
            ps
        }, keyHolder)

        val generatedId =
            keyHolder.key?.toLong()
                ?: throw IllegalStateException("Failed to get generated ID")

        return post.copy(id = generatedId)
    }

    override fun findById(id: Long): Post? {
        val sql = "SELECT * FROM posts WHERE id = ? AND deleted_at IS NULL"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    override fun findAllById(ids: List<Long>): List<Post> {
        if (ids.isEmpty()) return emptyList()
        if (ids.size > 20) {
            throw IllegalArgumentException("Maximum 20 post IDs allowed")
        }

        val placeholders = ids.joinToString(",") { "?" }
        val sql =
            """
            SELECT * FROM posts 
            WHERE id IN ($placeholders) AND deleted_at IS NULL
            ORDER BY FIELD(id, $placeholders)
            """.trimIndent()

        // FIELD() 함수로 요청 순서 유지
        val params = ids + ids // 두 번 넣어야 함 (IN, FIELD)
        return jdbcTemplate.query(sql, rowMapper, *params.toTypedArray())
    }

    override fun existsById(id: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM posts WHERE id = ? AND deleted_at IS NULL"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, id) ?: 0
        return count > 0
    }
}
