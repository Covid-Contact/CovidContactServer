package cat.covidcontact.server.data.user

import org.springframework.data.neo4j.repository.Neo4jRepository

interface UserRepository : Neo4jRepository<User, String> {
    fun findByEmail(email: String): User?
}
