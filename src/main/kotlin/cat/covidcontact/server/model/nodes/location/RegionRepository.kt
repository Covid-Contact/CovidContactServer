package cat.covidcontact.server.model.nodes.location

import org.springframework.data.neo4j.repository.Neo4jRepository

interface RegionRepository : Neo4jRepository<Region, String> {
    fun findRegionByName(name: String): Region?
}
