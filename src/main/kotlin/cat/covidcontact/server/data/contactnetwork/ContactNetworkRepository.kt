package cat.covidcontact.server.data.contactnetwork

import org.springframework.data.neo4j.repository.Neo4jRepository


interface ContactNetworkRepository : Neo4jRepository<ContactNetwork, String> {
    fun findContactNetworkByName(name: String): ContactNetwork?
}
