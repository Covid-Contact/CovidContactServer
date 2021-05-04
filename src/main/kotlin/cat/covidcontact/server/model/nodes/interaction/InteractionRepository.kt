package cat.covidcontact.server.model.nodes.interaction

import org.springframework.data.neo4j.repository.Neo4jRepository

interface InteractionRepository : Neo4jRepository<Interaction, Long> {

}
