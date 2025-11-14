package oliveyoung.user_service.domain.user.repository

import oliveyoung.user_service.domain.user.entity.User
import oliveyoung.user_service.domain.user.entity.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

@Repository
class UserRepository {
    
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    
    private val rowMapper = RowMapper { rs, _ ->
        User(
            id = rs.getLong("id"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            password = rs.getString("password"),
            imageUrl = rs.getString("image_url"),
            role = Role.valueOf(rs.getString("role")),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime(),
            deletedAt = rs.getTimestamp("deleted_at")?.toLocalDateTime()
        )
    }
    
    fun insert(user: User): User {
        require(user.id == null) { "Cannot insert user with existing id" }
        
        val sql = """
            INSERT INTO users (username, email, password, image_url, role, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, NOW(), NOW())
        """.trimIndent()
        
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, user.username)
            ps.setString(2, user.email)
            ps.setString(3, user.password)
            ps.setString(4, user.imageUrl)
            ps.setString(5, user.role.name)
            ps
        }, keyHolder)
        
        val generatedId = keyHolder.key?.toLong() ?: throw IllegalStateException("Failed to get generated ID")
        return user.copy(id = generatedId)
    }
    
    fun findById(id: Long): User? {
        val sql = "SELECT * FROM users WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }
    
    fun findByEmail(email: String): User? {
        val sql = "SELECT * FROM users WHERE email = ?"
        return jdbcTemplate.query(sql, rowMapper, email).firstOrNull()
    }
    
    fun findByUsername(username: String): User? {
        val sql = "SELECT * FROM users WHERE username = ?"
        return jdbcTemplate.query(sql, rowMapper, username).firstOrNull()
    }
    
    fun findAllById(ids: List<Long>): List<User> {
        if (ids.isEmpty()) return emptyList()

        val placeholders = ids.joinToString(",") { "?" }
        val sql = "SELECT * FROM users WHERE id IN ($placeholders)"
        return jdbcTemplate.query(sql, rowMapper, *ids.toTypedArray())
    }

    
    
    
    
    fun existsById(id: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM users WHERE id = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, id) ?: 0
        return count > 0
    }
}
