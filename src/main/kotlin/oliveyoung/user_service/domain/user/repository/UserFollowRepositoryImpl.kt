package oliveyoung.user_service.domain.user.repository

import oliveyoung.user_service.domain.user.entity.Follow
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

/**
 * UserFollowRepository JDBC Template 구현체
 * 
 * @Repository: Spring이 빈으로 등록
 * UserFollowRepository 인터페이스의 구현체로 자동 주입됨
 */
@Repository
class UserFollowRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : UserFollowRepository {

    private val rowMapper = RowMapper { rs, _ ->
        Follow(
            id = rs.getLong("id"),
            followerId = rs.getLong("follower_id"),
            followingId = rs.getLong("following_id"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime()
        )
    }

    override fun save(follow: Follow): Follow {
        val sql = """
            INSERT INTO follows (follower_id, following_id, created_at)
            VALUES (?, ?, NOW())
        """.trimIndent()

        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setLong(1, follow.followerId)
            ps.setLong(2, follow.followingId)
            ps
        }, keyHolder)

        val generatedId = keyHolder.key?.toLong() 
            ?: throw IllegalStateException("Failed to get generated ID")
        
        return follow.copy(id = generatedId)
    }

    override fun delete(follow: Follow) {
        val sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?"
        jdbcTemplate.update(sql, follow.followerId, follow.followingId)
    }

    override fun existsByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ? AND following_id = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, followerId, followingId) ?: 0
        return count > 0
    }

    override fun findByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Follow? {
        val sql = "SELECT * FROM follows WHERE follower_id = ? AND following_id = ?"
        return jdbcTemplate.query(sql, rowMapper, followerId, followingId).firstOrNull()
    }

    override fun findByFollowingIdOrderByIdDesc(followingId: Long, limit: Int): List<Follow> {
        val sql = """
            SELECT * FROM follows 
            WHERE following_id = ? 
            ORDER BY id DESC 
            LIMIT ?
        """.trimIndent()
        return jdbcTemplate.query(sql, rowMapper, followingId, limit)
    }

    override fun findByFollowingIdAndIdLessThanOrderByIdDesc(
        followingId: Long, 
        cursor: Long, 
        limit: Int
    ): List<Follow> {
        val sql = """
            SELECT * FROM follows 
            WHERE following_id = ? AND id < ? 
            ORDER BY id DESC 
            LIMIT ?
        """.trimIndent()
        return jdbcTemplate.query(sql, rowMapper, followingId, cursor, limit)
    }

    override fun findByFollowerIdOrderByIdDesc(followerId: Long, limit: Int): List<Follow> {
        val sql = """
            SELECT * FROM follows 
            WHERE follower_id = ? 
            ORDER BY id DESC 
            LIMIT ?
        """.trimIndent()
        return jdbcTemplate.query(sql, rowMapper, followerId, limit)
    }

    override fun findByFollowerIdAndIdLessThanOrderByIdDesc(
        followerId: Long, 
        cursor: Long, 
        limit: Int
    ): List<Follow> {
        val sql = """
            SELECT * FROM follows 
            WHERE follower_id = ? AND id < ? 
            ORDER BY id DESC 
            LIMIT ?
        """.trimIndent()
        return jdbcTemplate.query(sql, rowMapper, followerId, cursor, limit)
    }

    override fun findFollowerIdsByFollowingId(userId: Long): List<Long> {
        val sql = "SELECT follower_id FROM follows WHERE following_id = ?"
        return jdbcTemplate.queryForList(sql, Long::class.java, userId)
    }

    override fun findFollowingIdsByFollowerId(userId: Long): List<Long> {
        val sql = "SELECT following_id FROM follows WHERE follower_id = ?"
        return jdbcTemplate.queryForList(sql, Long::class.java, userId)
    }
}
