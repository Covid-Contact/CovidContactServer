package cat.covidcontact.server.model.nodes.location

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
data class Region(
    @Id
    var name: String,

    @Relationship(type = "HAS_PROVINCES", direction = Relationship.Direction.OUTGOING)
    var provinces: MutableList<Province> = mutableListOf()
)
