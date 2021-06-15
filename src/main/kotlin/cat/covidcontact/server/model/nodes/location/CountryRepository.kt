package cat.covidcontact.server.model.nodes.location

import org.springframework.data.neo4j.repository.Neo4jRepository

interface CountryRepository : Neo4jRepository<Country, String> {
    fun findCountryByName(name: String): Country?
}
