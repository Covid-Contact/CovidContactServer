package cat.covidcontact.server.model.nodes.user

import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param

interface UserRepository : Neo4jRepository<User, String> {

    fun findByEmail(email: String): User?

    fun existsUserByEmail(email: String): Boolean

    @Query("match (u:User {email: \$email})-[m:MEMBER]->(c:ContactNetwork {name: \$name}) detach delete m")
    fun removeMember(@Param("email") email: String, @Param("name") contactNetworkName: String)

    @Query("match (u:User)-[:MEMBER]->(c:ContactNetwork{name: \$contactNetworkName}) return u")
    fun getAllMembersFromContactNetwork(contactNetworkName: String): List<User>

    @Query("match (u:User)-[:MEMBER{isOwner: false}]->(c:ContactNetwork{name: \$contactNetworkName}}) return u")
    fun getAllNonOwnerMembersFromContactNetwork(contactNetworkName: String): List<User>
}
