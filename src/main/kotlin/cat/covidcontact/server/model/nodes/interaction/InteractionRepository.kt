package cat.covidcontact.server.model.nodes.interaction

import cat.covidcontact.server.model.nodes.user.User
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query

interface InteractionRepository : Neo4jRepository<Interaction, Long> {
    fun getInteractionsByContactNetworkName(name: String): List<Interaction>

    //@Query("match (i:Interaction)-[ui:USER_INTERACTIONS]->(u:User) return i, u")
    @Query("match (i:Interaction) where ID(i) = 15 return i")
    fun findAllInteractions(): List<Interaction>

    @Query("match (u:User) where ID(u) = 76 return u")
    fun findUsersFromInteraction(): List<User>
}
