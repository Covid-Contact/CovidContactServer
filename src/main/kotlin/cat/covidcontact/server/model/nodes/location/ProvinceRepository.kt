package cat.covidcontact.server.model.nodes.location

import org.springframework.data.neo4j.repository.Neo4jRepository

interface ProvinceRepository : Neo4jRepository<Province, String> {
    fun findProvinceByName(name: String): Province?
}
