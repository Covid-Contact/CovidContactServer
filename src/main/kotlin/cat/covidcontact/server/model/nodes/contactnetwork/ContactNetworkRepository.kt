package cat.covidcontact.server.model.nodes.contactnetwork

import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query


interface ContactNetworkRepository : Neo4jRepository<ContactNetwork, String> {
    fun findContactNetworkByName(name: String): ContactNetwork?
    fun existsContactNetworkByAccessCode(accessCode: String): Boolean
    fun findContactNetworkByAccessCode(accessCode: String): ContactNetwork?

    @Query("match (c:ContactNetwork{name: \$name}) set c.state = 'Deleted'")
    fun setStateToDeleted(name: String)
}
