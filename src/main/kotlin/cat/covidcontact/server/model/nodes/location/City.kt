package cat.covidcontact.server.model.nodes.location

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node
data class City(
    @Id
    var name: String
)
