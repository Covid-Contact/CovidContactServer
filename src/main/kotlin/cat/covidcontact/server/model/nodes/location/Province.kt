package cat.covidcontact.server.model.nodes.location

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
data class Province(
    @Id
    var name: String,

    @Relationship(type = "HAS_CITIES", direction = Relationship.Direction.OUTGOING)
    var cities: MutableList<City> = mutableListOf()
)
