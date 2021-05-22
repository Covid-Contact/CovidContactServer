package cat.covidcontact.server.model.nodes.contactnetwork

import org.springframework.data.neo4j.repository.Neo4jRepository


interface ContactNetworkRepository : Neo4jRepository<ContactNetwork, String> {
    fun findContactNetworkByName(name: String): ContactNetwork?

    fun existsContactNetworkByAccessCode(accessCode: String): Boolean

    fun findContactNetworkByAccessCode(accessCode: String): ContactNetwork?

    fun removeContactNetworkByName(name: String)
}
